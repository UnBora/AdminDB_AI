package com.platform.modules.audit.dto.request;

import com.platform.modules.audit.entity.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditSearchRequest {
    private String entityType;
    private UUID entityId;
    private UUID userId;
    private AuditAction action;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
}
