package com.platform.modules.report.mapper;

import com.platform.modules.report.dto.request.ReportRequest;
import com.platform.modules.report.dto.response.ReportResponse;
import com.platform.modules.report.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "type", expression = "java(com.platform.modules.report.entity.Report.ReportType.valueOf(dto.getType()))")
    @Mapping(target = "format", expression = "java(com.platform.modules.report.entity.Report.ReportFormat.valueOf(dto.getFormat()))")
    Report toEntity(ReportRequest dto);

    @Mapping(target = "type", expression = "java(entity.getType().toString())")
    @Mapping(target = "format", expression = "java(entity.getFormat().toString())")
    ReportResponse toResponse(Report entity);
}
