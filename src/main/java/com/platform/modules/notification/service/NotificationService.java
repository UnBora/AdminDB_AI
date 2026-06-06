package com.platform.modules.notification.service;

import com.platform.core.base.BaseService;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.notification.dto.request.NotificationCreateRequest;
import com.platform.modules.notification.dto.response.NotificationListResponse;
import com.platform.modules.notification.dto.response.NotificationPreferenceResponse;
import com.platform.modules.notification.dto.response.NotificationResponse;
import com.platform.modules.notification.entity.Notification;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService extends BaseService<Notification, NotificationResponse> {

    NotificationResponse createNotification(NotificationCreateRequest request);

    NotificationListResponse getNotifications(UUID userId, int page, int size, String sortBy, String sortDirection);

    List<NotificationResponse> getUnreadNotifications(UUID userId);

    NotificationResponse markAsRead(UUID notificationId);

    void markAllAsRead(UUID userId);

    void deleteNotification(UUID notificationId);

    long getUnreadCount(UUID userId);

    void sendSystemNotification(List<UUID> recipientIds, String title, String message);

    void sendSystemNotificationToUser(UUID userId, String title, String message);

    List<NotificationPreferenceResponse> getUserPreferences(UUID userId);

    NotificationPreferenceResponse updatePreference(UUID userId, String notificationType, NotificationPreferenceResponse request);

    void cleanupExpiredNotifications();
}
