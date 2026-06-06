package com.platform.modules.dashboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidgetRequest {

    private UUID userId;

    @NotBlank(message = "Widget type is required")
    @Size(max = 100, message = "Widget type must not exceed 100 characters")
    private String widgetType;

    @Size(max = 255, message = "Widget title must not exceed 255 characters")
    private String widgetTitle;

    private Integer position;

    private Boolean visible;

    private String configuration;
}
