package com.platform.modules.audit.controller;

import com.platform.core.base.ApiResponse;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.audit.dto.request.AuditSearchRequest;
import com.platform.modules.audit.dto.response.AuditHistoryResponse;
import com.platform.modules.audit.dto.response.AuditLogResponse;
import com.platform.modules.audit.entity.AuditAction;
import com.platform.modules.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    /**
     * GET /api/audit/entity/{entityId} - Get audit history for a specific entity
     */
    @GetMapping("/entity/{entityId}")
    public ResponseEntity<ApiResponse<AuditHistoryResponse>> getEntityAuditHistory(
            @PathVariable UUID entityId,
            @RequestParam(required = false, defaultValue = "Entity") String entityType) {

        AuditHistoryResponse history = auditService.getEntityAuditHistory(entityId, entityType);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * GET /api/audit/user/{userId} - Get audit entries for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getUserAuditHistory(
            @PathVariable UUID userId) {

        List<AuditLogResponse> history = auditService.getUserAuditHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * GET /api/audit/logs - Search audit logs with pagination and filtering
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<PaginationResponse<AuditLogResponse>>> searchAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        AuditSearchRequest request = AuditSearchRequest.builder()
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .action(action)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PaginationResponse<AuditLogResponse> result = auditService.getAuditTrail(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/audit/export - Export audit logs as CSV
     */
    @GetMapping("/export")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> exportAuditLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AuditLogResponse> logs = auditService.exportAuditLog(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * GET /api/audit/log/{id} - Get a specific audit log entry
     */
    @GetMapping("/log/{id}")
    public ResponseEntity<ApiResponse<AuditLogResponse>> getAuditLog(@PathVariable UUID id) {
        AuditLogResponse log = auditService.getAuditLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(log));
    }

    /**
     * GET /api/audit/stats - Get audit statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getAuditStats() {
        Map<String, Long> stats = Map.of(
                "totalAuditLogs", auditService.getAuditLogCount()
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getActionHistory(
            @PathVariable AuditAction action) {

        List<AuditLogResponse> history = auditService.getActionHistory(action);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
