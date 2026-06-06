package com.platform.modules.settings.service;

import com.platform.modules.settings.dto.response.SettingResponse;
import com.platform.modules.settings.entity.AppSetting;
import com.platform.modules.settings.entity.SettingCategory;
import com.platform.modules.settings.entity.SettingKey;

import java.util.List;
import java.util.Optional;

public interface SettingService {
    
    SettingResponse getSetting(String key);
    
    SettingResponse getSetting(String key, Object defaultValue);
    
    Optional<SettingResponse> getSettingOptional(String key);
    
    List<SettingResponse> getSettingsByCategory(SettingCategory category);
    
    List<SettingResponse> getAllSettings();
    
    SettingResponse setSetting(String key, Object value);
    
    SettingResponse setSetting(String key, Object value, String category, String description);
    
    void deleteSetting(String key);
    
    void cacheSettings();
    
    void refreshCache();
    
    Boolean getSettingAsBoolean(String key);
    
    Boolean getSettingAsBoolean(String key, Boolean defaultValue);
    
    Integer getSettingAsInt(String key);
    
    Integer getSettingAsInt(String key, Integer defaultValue);
    
    String getSettingAsString(String key);
    
    String getSettingAsString(String key, String defaultValue);
    
    Double getSettingAsDouble(String key);
    
    Double getSettingAsDouble(String key, Double defaultValue);
    
    boolean testSmtpConnection();
    
    void initializeDefaultSettings();
}
