package com.platform.modules.menu.mapper;

import com.platform.modules.menu.dto.request.MenuCreateRequest;
import com.platform.modules.menu.dto.request.MenuUpdateRequest;
import com.platform.modules.menu.dto.response.MenuResponse;
import com.platform.modules.menu.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    Menu toEntity(MenuCreateRequest request);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    void updateEntity(MenuUpdateRequest request, @MappingTarget Menu menu);

    @Mapping(target = "children", ignore = true)
    MenuResponse toResponse(Menu entity);
}

