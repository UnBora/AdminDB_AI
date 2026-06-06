package com.platform.modules.audit.dto.response;

import com.platform.modules.audit.entity.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private UUID id;
    private String entityType;
    private UUID entityId;
    private AuditAction action;
    private UUID userId;
    private String ipAddress;
    private String userAgent;
    private String description;
    private Map<String, Object> oldData;
    private Map<String, Object> newData;
    private Map<String, Map<String, Object>> changes;
    private LocalDateTime createdAt;
}
