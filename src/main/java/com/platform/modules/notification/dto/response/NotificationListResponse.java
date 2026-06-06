package com.platform.modules.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.modules.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationListResponse {

    private List<NotificationResponse> notifications;

    private long unreadCount;

    private int pageNumber;

    private int pageSize;

    private long totalElements;

    private int totalPages;
}
