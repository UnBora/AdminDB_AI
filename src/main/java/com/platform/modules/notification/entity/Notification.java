package com.platform.modules.notification.entity;

import com.platform.core.base.BaseEntity;
import com.platform.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_read", columnList = "read"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "read")
    private Boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "data", columnDefinition = "jsonb")
    private String data;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.read = false;
        this.readAt = null;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
