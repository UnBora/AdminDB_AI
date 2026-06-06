package com.platform.modules.dashboard.mapper;

import com.platform.modules.dashboard.dto.request.DashboardWidgetRequest;
import com.platform.modules.dashboard.dto.response.DashboardWidgetResponse;
import com.platform.modules.dashboard.entity.DashboardWidget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DashboardWidgetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "user", ignore = true)
    DashboardWidget toEntity(DashboardWidgetRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(DashboardWidgetRequest request, @MappingTarget DashboardWidget widget);

    DashboardWidgetResponse toResponse(DashboardWidget entity);
}
