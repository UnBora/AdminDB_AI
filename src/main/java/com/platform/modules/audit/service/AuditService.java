package com.platform.modules.audit.service;

import com.platform.core.base.PaginationResponse;
import com.platform.modules.audit.dto.request.AuditSearchRequest;
import com.platform.modules.audit.dto.response.AuditHistoryResponse;
import com.platform.modules.audit.dto.response.AuditLogResponse;
import com.platform.modules.audit.entity.AuditAction;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AuditService {

    void logAction(String entityType, UUID entityId, AuditAction action, UUID userId,
                   Map<String, Object> oldData, Map<String, Object> newData,
                   String description, String ipAddress, String userAgent);

    void logLogin(UUID userId, String ipAddress, String userAgent, boolean success);

    void logLogout(UUID userId, String ipAddress);

    void logPermissionChange(UUID userId, UUID targetUserId, List<String> oldRoles,
                            List<String> newRoles, String ipAddress, String userAgent);

    AuditHistoryResponse getEntityAuditHistory(UUID entityId, String entityType);

    PaginationResponse<AuditLogResponse> getAuditTrail(AuditSearchRequest request);

    List<AuditLogResponse> exportAuditLog(LocalDateTime startDate, LocalDateTime endDate);

    AuditLogResponse getAuditLogById(UUID auditLogId);

    List<AuditLogResponse> getUserAuditHistory(UUID userId);

    List<AuditLogResponse> getActionHistory(AuditAction action);

    long getAuditLogCount();

    long getAuditLogCountByEntity(UUID entityId);

    long getAuditLogCountByUser(UUID userId);
}
