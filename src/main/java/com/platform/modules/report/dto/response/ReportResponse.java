package com.platform.modules.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private UUID id;
    private String name;
    private String description;
    private String type;
    private String format;
    private Boolean isScheduled;
    private String scheduleFrequency;
    private LocalDateTime lastRunAt;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
