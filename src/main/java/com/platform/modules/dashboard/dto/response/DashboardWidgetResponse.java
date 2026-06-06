package com.platform.modules.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardWidgetResponse {

    private UUID id;

    private String widgetType;

    private String widgetTitle;

    private Integer position;

    private Boolean visible;

    private String configuration;
}
