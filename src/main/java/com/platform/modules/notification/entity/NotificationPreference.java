package com.platform.modules.notification.entity;

import com.platform.core.base.BaseEntity;
import com.platform.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences", indexes = {
    @Index(name = "idx_notification_preferences_user_id", columnList = "user_id"),
    @Index(name = "idx_notification_preferences_type", columnList = "notification_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(name = "email_enabled")
    private Boolean emailEnabled = true;

    @Column(name = "push_enabled")
    private Boolean pushEnabled = true;

    @Column(name = "in_app_enabled")
    private Boolean inAppEnabled = true;

    @Column(name = "enabled")
    private Boolean enabled = true;
}
