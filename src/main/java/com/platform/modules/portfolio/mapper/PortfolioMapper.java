package com.platform.modules.portfolio.mapper;

import com.platform.modules.portfolio.dto.request.PortfolioRequest;
import com.platform.modules.portfolio.dto.response.PortfolioResponse;
import com.platform.modules.portfolio.entity.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Portfolio toEntity(PortfolioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntity(PortfolioRequest request, @MappingTarget Portfolio portfolio);

    PortfolioResponse toResponse(Portfolio entity);
}
