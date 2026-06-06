package com.platform.modules.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.modules.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPreferenceResponse {

    private String notificationType;

    private Boolean emailEnabled;

    private Boolean pushEnabled;

    private Boolean inAppEnabled;

    private Boolean enabled;
}
