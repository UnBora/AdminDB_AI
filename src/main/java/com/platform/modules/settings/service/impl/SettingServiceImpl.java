package com.platform.modules.settings.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.settings.dto.response.SettingResponse;
import com.platform.modules.settings.entity.AppSetting;
import com.platform.modules.settings.entity.SettingCategory;
import com.platform.modules.settings.entity.SettingKey;
import com.platform.modules.settings.repository.AppSettingRepository;
import com.platform.modules.settings.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final AppSettingRepository appSettingRepository;
    private final ObjectMapper objectMapper;
    private final JavaMailSender javaMailSender;
    
    private final ConcurrentHashMap<String, SettingResponse> settingsCache = new ConcurrentHashMap<>();
    private volatile boolean cacheInitialized = false;

    @Override
    public SettingResponse getSetting(String key) {
        return getSetting(key, null);
    }

    @Override
    public SettingResponse getSetting(String key, Object defaultValue) {
        SettingResponse setting = getSettingOptional(key).orElse(null);
        
        if (setting == null && defaultValue != null) {
            return SettingResponse.builder()
                    .settingKey(key)
                    .settingValue(defaultValue)
                    .build();
        }
        
        if (setting == null) {
            throw new ResourceNotFoundException("Setting not found: " + key);
        }
        
        return setting;
    }

    @Override
    public Optional<SettingResponse> getSettingOptional(String key) {
        ensureCacheInitialized();
        return Optional.ofNullable(settingsCache.get(key));
    }

    @Override
    public List<SettingResponse> getSettingsByCategory(SettingCategory category) {
        ensureCacheInitialized();
        return settingsCache.values().stream()
                .filter(s -> category.name().equals(s.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SettingResponse> getAllSettings() {
        ensureCacheInitialized();
        return new ArrayList<>(settingsCache.values());
    }

    @Override
    @CacheEvict(value = "settings", allEntries = true)
    public SettingResponse setSetting(String key, Object value) {
        return setSetting(key, value, null, null);
    }

    @Override
    @CacheEvict(value = "settings", allEntries = true)
    public SettingResponse setSetting(String key, Object value, String category, String description) {
        Optional<AppSetting> existing = appSettingRepository.findBySettingKey(key);
        
        AppSetting appSetting;
        if (existing.isPresent()) {
            appSetting = existing.get();
            appSetting.setSettingValue(serializeValue(value));
            if (category != null) {
                appSetting.setCategory(category);
            }
            if (description != null) {
                appSetting.setDescription(description);
            }
        } else {
            appSetting = AppSetting.builder()
                    .settingKey(key)
                    .settingValue(serializeValue(value))
                    .category(category != null ? category : getDefaultCategory(key))
                    .description(description)
                    .build();
        }
        
        AppSetting saved = appSettingRepository.save(appSetting);
        SettingResponse response = mapToResponse(saved);
        settingsCache.put(key, response);
        
        log.info("Setting updated: {} = {}", key, value);
        
        return response;
    }

    @Override
    @CacheEvict(value = "settings", allEntries = true)
    public void deleteSetting(String key) {
        AppSetting appSetting = appSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found: " + key));
        
        appSettingRepository.delete(appSetting);
        settingsCache.remove(key);
        
        log.info("Setting deleted: {}", key);
    }

    @Override
    public void cacheSettings() {
        synchronized (settingsCache) {
            settingsCache.clear();
            List<AppSetting> allSettings = appSettingRepository.findAllActive();
            
            for (AppSetting setting : allSettings) {
                SettingResponse response = mapToResponse(setting);
                settingsCache.put(setting.getSettingKey(), response);
            }
            
            cacheInitialized = true;
            log.info("Settings cache initialized with {} entries", settingsCache.size());
        }
    }

    @Override
    @CacheEvict(value = "settings", allEntries = true)
    public void refreshCache() {
        cacheSettings();
        log.info("Settings cache refreshed");
    }

    @Override
    public Boolean getSettingAsBoolean(String key) {
        return getSettingAsBoolean(key, false);
    }

    @Override
    public Boolean getSettingAsBoolean(String key, Boolean defaultValue) {
        SettingResponse setting = getSettingOptional(key).orElse(null);
        
        if (setting == null) {
            return defaultValue;
        }
        
        Object value = setting.getSettingValue();
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        if (value instanceof String) {
            String strValue = (String) value;
            return "true".equalsIgnoreCase(strValue) || "1".equals(strValue) || "yes".equalsIgnoreCase(strValue);
        }
        
        return defaultValue;
    }

    @Override
    public Integer getSettingAsInt(String key) {
        return getSettingAsInt(key, 0);
    }

    @Override
    public Integer getSettingAsInt(String key, Integer defaultValue) {
        SettingResponse setting = getSettingOptional(key).orElse(null);
        
        if (setting == null) {
            return defaultValue;
        }
        
        Object value = setting.getSettingValue();
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse setting {} as integer: {}", key, value);
                return defaultValue;
            }
        }
        
        return defaultValue;
    }

    @Override
    public String getSettingAsString(String key) {
        return getSettingAsString(key, "");
    }

    @Override
    public String getSettingAsString(String key, String defaultValue) {
        SettingResponse setting = getSettingOptional(key).orElse(null);
        
        if (setting == null) {
            return defaultValue;
        }
        
        Object value = setting.getSettingValue();
        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public Double getSettingAsDouble(String key) {
        return getSettingAsDouble(key, 0.0);
    }

    @Override
    public Double getSettingAsDouble(String key, Double defaultValue) {
        SettingResponse setting = getSettingOptional(key).orElse(null);
        
        if (setting == null) {
            return defaultValue;
        }
        
        Object value = setting.getSettingValue();
        if (value instanceof Double) {
            return (Double) value;
        }
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse setting {} as double: {}", key, value);
                return defaultValue;
            }
        }
        
        return defaultValue;
    }

    @Override
    public boolean testSmtpConnection() {
        try {
            String smtpHost = getSettingAsString(SettingKey.SMTP_HOST.getKey(), null);
            String smtpPort = getSettingAsString(SettingKey.SMTP_PORT.getKey(), null);
            String smtpUsername = getSettingAsString(SettingKey.SMTP_USERNAME.getKey(), null);
            String smtpPassword = getSettingAsString(SettingKey.SMTP_PASSWORD.getKey(), null);
            String fromEmail = getSettingAsString(SettingKey.SMTP_FROM_EMAIL.getKey(), null);
            
            if (smtpHost == null || smtpPort == null) {
                throw new BusinessException("SMTP configuration is incomplete");
            }
            
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(fromEmail != null ? fromEmail : smtpUsername);
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, smtpUsername);
            message.setSubject("SMTP Test");
            message.setText("This is a test message to verify SMTP configuration.");
            
            javaMailSender.send(message);
            
            log.info("SMTP connection test successful");
            return true;
        } catch (Exception e) {
            log.error("SMTP connection test failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void initializeDefaultSettings() {
        // General Settings
        createDefaultSetting(SettingKey.APP_NAME, "AdminDB AI", SettingCategory.GENERAL, "Application name");
        createDefaultSetting(SettingKey.APP_DESCRIPTION, "Enterprise Administration Platform", SettingCategory.GENERAL, "Application description");
        createDefaultSetting(SettingKey.APP_LOGO_URL, "/images/logo.png", SettingCategory.GENERAL, "Application logo URL");
        
        // Appearance Settings
        createDefaultSetting(SettingKey.APP_THEME, "light", SettingCategory.APPEARANCE, "Application theme (light/dark/auto)");
        createDefaultSetting(SettingKey.DARK_MODE_ENABLED, false, SettingCategory.APPEARANCE, "Enable dark mode");
        createDefaultSetting(SettingKey.PRIMARY_COLOR, "#007bff", SettingCategory.APPEARANCE, "Primary color in hex");
        createDefaultSetting(SettingKey.SECONDARY_COLOR, "#6c757d", SettingCategory.APPEARANCE, "Secondary color in hex");
        
        // Email/SMTP Settings
        createDefaultSetting(SettingKey.SMTP_HOST, "smtp.gmail.com", SettingCategory.EMAIL, "SMTP server host");
        createDefaultSetting(SettingKey.SMTP_PORT, 587, SettingCategory.EMAIL, "SMTP server port");
        createDefaultSetting(SettingKey.SMTP_USERNAME, "", SettingCategory.EMAIL, "SMTP username");
        createDefaultSetting(SettingKey.SMTP_PASSWORD, "", SettingCategory.EMAIL, "SMTP password");
        createDefaultSetting(SettingKey.SMTP_FROM_EMAIL, "", SettingCategory.EMAIL, "SMTP sender email");
        
        // Security Settings
        createDefaultSetting(SettingKey.ENABLE_2FA, false, SettingCategory.SECURITY, "Enable two-factor authentication");
        createDefaultSetting(SettingKey.PASSWORD_MIN_LENGTH, 8, SettingCategory.SECURITY, "Minimum password length");
        createDefaultSetting(SettingKey.PASSWORD_REQUIRE_NUMBERS, true, SettingCategory.SECURITY, "Require numbers in password");
        createDefaultSetting(SettingKey.PASSWORD_REQUIRE_UPPERCASE, true, SettingCategory.SECURITY, "Require uppercase in password");
        createDefaultSetting(SettingKey.PASSWORD_REQUIRE_SPECIAL_CHARS, true, SettingCategory.SECURITY, "Require special characters in password");
        createDefaultSetting(SettingKey.SESSION_TIMEOUT_MINUTES, 30, SettingCategory.SECURITY, "Session timeout in minutes");
        createDefaultSetting(SettingKey.PASSWORD_EXPIRY_DAYS, 90, SettingCategory.SECURITY, "Password expiry in days");
        createDefaultSetting(SettingKey.ENABLE_PASSWORD_EXPIRY, false, SettingCategory.SECURITY, "Enable password expiry policy");
        
        // System Settings
        createDefaultSetting(SettingKey.LANGUAGE, "en", SettingCategory.SYSTEM, "Default language (en/km/ko)");
        createDefaultSetting(SettingKey.TIMEZONE, "Asia/Phnom_Penh", SettingCategory.SYSTEM, "Application timezone");
        createDefaultSetting(SettingKey.DATE_FORMAT, "DD/MM/YYYY", SettingCategory.SYSTEM, "Date format");
        createDefaultSetting(SettingKey.TIME_FORMAT, "24h", SettingCategory.SYSTEM, "Time format (12h/24h)");
        createDefaultSetting(SettingKey.MAINTENANCE_MODE, false, SettingCategory.SYSTEM, "Enable maintenance mode");
        
        // Application Settings
        createDefaultSetting(SettingKey.MAX_FILE_UPLOAD_SIZE, 100, SettingCategory.APPLICATION, "Max file upload size in MB");
        createDefaultSetting(SettingKey.BACKUP_FREQUENCY, "daily", SettingCategory.APPLICATION, "Backup frequency (daily/weekly)");
        createDefaultSetting(SettingKey.BACKUP_RETENTION_DAYS, 30, SettingCategory.APPLICATION, "Backup retention in days");
        
        // Advanced Settings
        createDefaultSetting(SettingKey.AUDIT_LOG_RETENTION_DAYS, 90, SettingCategory.ADVANCED, "Audit log retention in days");
        createDefaultSetting(SettingKey.AUDIT_LOG_ENABLED, true, SettingCategory.ADVANCED, "Enable audit logging");
        
        log.info("Default settings initialized");
    }

    private void createDefaultSetting(SettingKey settingKey, Object value, SettingCategory category, String description) {
        if (!appSettingRepository.existsBySettingKey(settingKey.getKey())) {
            AppSetting setting = AppSetting.builder()
                    .settingKey(settingKey.getKey())
                    .settingValue(serializeValue(value))
                    .category(category.name())
                    .description(description)
                    .build();
            
            appSettingRepository.save(setting);
        }
    }

    private String serializeValue(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof String) {
            return (String) value;
        }
        
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("Failed to serialize value: {}", e.getMessage());
            return value.toString();
        }
    }

    private Object deserializeValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        // Try to parse as JSON
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (Exception e) {
            // If not JSON, return as string
            return value;
        }
    }

    private SettingResponse mapToResponse(AppSetting appSetting) {
        return SettingResponse.builder()
                .settingKey(appSetting.getSettingKey())
                .settingValue(deserializeValue(appSetting.getSettingValue()))
                .category(appSetting.getCategory())
                .description(appSetting.getDescription())
                .build();
    }

    private String getDefaultCategory(String key) {
        try {
            SettingKey settingKey = SettingKey.fromString(key);
            return settingKey.getCategory().name();
        } catch (IllegalArgumentException e) {
            return SettingCategory.GENERAL.name();
        }
    }

    private void ensureCacheInitialized() {
        if (!cacheInitialized) {
            synchronized (settingsCache) {
                if (!cacheInitialized) {
                    cacheSettings();
                }
            }
        }
    }
}
