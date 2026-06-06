package com.platform.core.constants;

public final class AppConstants {

    public static final String DEFAULT_LOCALE = "en";
    public static final String[] SUPPORTED_LOCALES = {"en", "km", "ko"};

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MANAGER = "MANAGER";

    // Audit Actions
    public static final String AUDIT_CREATE = "CREATE";
    public static final String AUDIT_UPDATE = "UPDATE";
    public static final String AUDIT_DELETE = "DELETE";
    public static final String AUDIT_LOGIN = "LOGIN";
    public static final String AUDIT_LOGOUT = "LOGOUT";

    // Settings Keys
    public static final String SETTING_THEME = "theme";
    public static final String SETTING_DARK_MODE = "darkMode";
    public static final String SETTING_LANGUAGE = "language";

    // File Upload
    public static final long MAX_FILE_SIZE = 52428800; // 50MB
    public static final String UPLOAD_DIR = "uploads";

    // Notification Types
    public static final String NOTIFICATION_INFO = "INFO";
    public static final String NOTIFICATION_WARNING = "WARNING";
    public static final String NOTIFICATION_ERROR = "ERROR";
    public static final String NOTIFICATION_SUCCESS = "SUCCESS";

    private AppConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
