package com.platform.modules.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditHistoryResponse {
    private String entityType;
    private Object entityId;
    private long totalChanges;
    private List<AuditLogResponse> auditLogs;
    private String summary;
}
