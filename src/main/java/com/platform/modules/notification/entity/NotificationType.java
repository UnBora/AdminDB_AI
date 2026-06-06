package com.platform.modules.notification.entity;

public enum NotificationType {
    USER_MESSAGE("User-to-user messages"),
    SYSTEM_ALERT("System notifications"),
    TASK_ASSIGNED("Task assignment"),
    REPORT_READY("Report generation complete"),
    PERMISSION_CHANGED("User permission changes"),
    ACCOUNT_SECURITY("Security alerts");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
