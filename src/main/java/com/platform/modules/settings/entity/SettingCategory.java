package com.platform.modules.settings.entity;

public enum SettingCategory {
    GENERAL("General"),
    APPEARANCE("Appearance"),
    EMAIL("Email & SMTP"),
    SECURITY("Security"),
    SYSTEM("System"),
    APPLICATION("Application"),
    ADVANCED("Advanced");

    private final String displayName;

    SettingCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SettingCategory fromString(String category) {
        for (SettingCategory cat : SettingCategory.values()) {
            if (cat.name().equals(category)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Unknown setting category: " + category);
    }
}
