package com.platform.modules.dashboard.service;

import com.platform.core.base.BaseService;
import com.platform.modules.dashboard.dto.request.DashboardWidgetRequest;
import com.platform.modules.dashboard.dto.response.DashboardWidgetResponse;
import com.platform.modules.dashboard.entity.DashboardWidget;

import java.util.List;
import java.util.UUID;

public interface DashboardService extends BaseService<DashboardWidget, DashboardWidgetResponse> {
    
    DashboardWidgetResponse create(DashboardWidgetRequest request);
    
    DashboardWidgetResponse update(UUID id, DashboardWidgetRequest request);
    
    List<DashboardWidgetResponse> getUserWidgets(UUID userId);
    
    List<DashboardWidgetResponse> getVisibleUserWidgets(UUID userId);
    
    void updateWidgetPositions(List<UUID> widgetIds);
}
