package com.platform.modules.settings.entity;

public enum SettingKey {
    // General Settings
    APP_NAME("APP_NAME", SettingCategory.GENERAL),
    APP_DESCRIPTION("APP_DESCRIPTION", SettingCategory.GENERAL),
    APP_LOGO_URL("APP_LOGO_URL", SettingCategory.GENERAL),

    // Appearance Settings
    APP_THEME("APP_THEME", SettingCategory.APPEARANCE),
    DARK_MODE_ENABLED("DARK_MODE_ENABLED", SettingCategory.APPEARANCE),
    PRIMARY_COLOR("PRIMARY_COLOR", SettingCategory.APPEARANCE),
    SECONDARY_COLOR("SECONDARY_COLOR", SettingCategory.APPEARANCE),

    // Email/SMTP Settings
    SMTP_HOST("SMTP_HOST", SettingCategory.EMAIL),
    SMTP_PORT("SMTP_PORT", SettingCategory.EMAIL),
    SMTP_USERNAME("SMTP_USERNAME", SettingCategory.EMAIL),
    SMTP_PASSWORD("SMTP_PASSWORD", SettingCategory.EMAIL),
    SMTP_FROM_EMAIL("SMTP_FROM_EMAIL", SettingCategory.EMAIL),

    // Security Settings
    ENABLE_2FA("ENABLE_2FA", SettingCategory.SECURITY),
    PASSWORD_MIN_LENGTH("PASSWORD_MIN_LENGTH", SettingCategory.SECURITY),
    PASSWORD_REQUIRE_NUMBERS("PASSWORD_REQUIRE_NUMBERS", SettingCategory.SECURITY),
    PASSWORD_REQUIRE_UPPERCASE("PASSWORD_REQUIRE_UPPERCASE", SettingCategory.SECURITY),
    PASSWORD_REQUIRE_SPECIAL_CHARS("PASSWORD_REQUIRE_SPECIAL_CHARS", SettingCategory.SECURITY),
    SESSION_TIMEOUT_MINUTES("SESSION_TIMEOUT_MINUTES", SettingCategory.SECURITY),
    PASSWORD_EXPIRY_DAYS("PASSWORD_EXPIRY_DAYS", SettingCategory.SECURITY),
    ENABLE_PASSWORD_EXPIRY("ENABLE_PASSWORD_EXPIRY", SettingCategory.SECURITY),

    // System Settings
    LANGUAGE("LANGUAGE", SettingCategory.SYSTEM),
    TIMEZONE("TIMEZONE", SettingCategory.SYSTEM),
    DATE_FORMAT("DATE_FORMAT", SettingCategory.SYSTEM),
    TIME_FORMAT("TIME_FORMAT", SettingCategory.SYSTEM),
    MAINTENANCE_MODE("MAINTENANCE_MODE", SettingCategory.SYSTEM),

    // Application Settings
    MAX_FILE_UPLOAD_SIZE("MAX_FILE_UPLOAD_SIZE", SettingCategory.APPLICATION),
    BACKUP_FREQUENCY("BACKUP_FREQUENCY", SettingCategory.APPLICATION),
    BACKUP_RETENTION_DAYS("BACKUP_RETENTION_DAYS", SettingCategory.APPLICATION),

    // Advanced Settings
    AUDIT_LOG_RETENTION_DAYS("AUDIT_LOG_RETENTION_DAYS", SettingCategory.ADVANCED),
    AUDIT_LOG_ENABLED("AUDIT_LOG_ENABLED", SettingCategory.ADVANCED);

    private final String key;
    private final SettingCategory category;

    SettingKey(String key, SettingCategory category) {
        this.key = key;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public SettingCategory getCategory() {
        return category;
    }

    public static SettingKey fromString(String key) {
        for (SettingKey settingKey : SettingKey.values()) {
            if (settingKey.key.equals(key)) {
                return settingKey;
            }
        }
        throw new IllegalArgumentException("Unknown setting key: " + key);
    }
}
