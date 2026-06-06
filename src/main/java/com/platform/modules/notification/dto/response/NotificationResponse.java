package com.platform.modules.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.modules.notification.entity.NotificationPriority;
import com.platform.modules.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private UUID id;

    private UUID userId;

    private String title;

    private String message;

    private NotificationType type;

    private Boolean read;

    private LocalDateTime readAt;

    private String actionUrl;

    private String data;

    private NotificationPriority priority;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getFormattedTime() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
