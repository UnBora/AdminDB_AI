package com.platform.modules.notification.entity;

public enum NotificationPriority {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    URGENT(3);

    private final int level;

    NotificationPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
