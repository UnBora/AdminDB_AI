package com.platform.modules.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    @NotBlank(message = "Report name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Report type is required")
    private String type;

    @NotBlank(message = "Report format is required")
    private String format;

    private String queryBuilder;
    private Boolean isScheduled = false;
    private String scheduleFrequency;
}
