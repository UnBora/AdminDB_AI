package com.platform.modules.audit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.audit.dto.request.AuditSearchRequest;
import com.platform.modules.audit.dto.response.AuditHistoryResponse;
import com.platform.modules.audit.dto.response.AuditLogResponse;
import com.platform.modules.audit.entity.AuditAction;
import com.platform.modules.audit.entity.AuditLog;
import com.platform.modules.audit.repository.AuditRepository;
import com.platform.modules.audit.service.AuditService;
import com.platform.modules.audit.util.ChangeExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void logAction(String entityType, UUID entityId, AuditAction action, UUID userId,
                         Map<String, Object> oldData, Map<String, Object> newData,
                         String description, String ipAddress, String userAgent) {

        try {
            Map<String, Map<String, Object>> changes = ChangeExtractor.extractChanges(oldData, newData);
            
            String oldDataJson = oldData != null ? objectMapper.writeValueAsString(oldData) : null;
            String newDataJson = newData != null ? objectMapper.writeValueAsString(newData) : null;
            String changesJson = changes != null ? objectMapper.writeValueAsString(changes) : null;

            AuditLog auditLog = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .userId(userId)
                    .oldData(oldDataJson)
                    .newData(newDataJson)
                    .changes(changesJson)
                    .description(description)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            auditRepository.save(auditLog);
            log.debug("Audit log created for {} {} with action {}", entityType, entityId, action);
        } catch (Exception e) {
            log.error("Error logging audit action", e);
        }
    }

    @Override
    public void logLogin(UUID userId, String ipAddress, String userAgent, boolean success) {
        String description = success ? "User login successful" : "User login failed";

        AuditLog auditLog = AuditLog.builder()
                .entityType("User")
                .entityId(userId)
                .action(AuditAction.LOGIN)
                .userId(userId)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditRepository.save(auditLog);
        log.debug("Login audit logged for user {}", userId);
    }

    @Override
    public void logLogout(UUID userId, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .entityType("User")
                .entityId(userId)
                .action(AuditAction.LOGOUT)
                .userId(userId)
                .description("User logout")
                .ipAddress(ipAddress)
                .build();

        auditRepository.save(auditLog);
        log.debug("Logout audit logged for user {}", userId);
    }

    @Override
    public void logPermissionChange(UUID userId, UUID targetUserId, List<String> oldRoles,
                                   List<String> newRoles, String ipAddress, String userAgent) {

        try {
            Map<String, Object> oldData = Map.of("roles", oldRoles);
            Map<String, Object> newData = Map.of("roles", newRoles);
            Map<String, Map<String, Object>> changes = ChangeExtractor.extractChanges(oldData, newData);

            String oldDataJson = objectMapper.writeValueAsString(oldData);
            String newDataJson = objectMapper.writeValueAsString(newData);
            String changesJson = changes != null ? objectMapper.writeValueAsString(changes) : null;

            AuditLog auditLog = AuditLog.builder()
                    .entityType("User")
                    .entityId(targetUserId)
                    .action(AuditAction.PERMISSION_CHANGE)
                    .userId(userId)
                    .oldData(oldDataJson)
                    .newData(newDataJson)
                    .changes(changesJson)
                    .description("Permission/Role change")
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            auditRepository.save(auditLog);
            log.debug("Permission change audited for user {} by user {}", targetUserId, userId);
        } catch (Exception e) {
            log.error("Error logging permission change", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditHistoryResponse getEntityAuditHistory(UUID entityId, String entityType) {
        List<AuditLog> auditLogs = auditRepository.findByEntityIdOrderByCreatedAtDesc(entityId);

        List<AuditLogResponse> responses = auditLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return AuditHistoryResponse.builder()
                .entityType(entityType)
                .entityId(entityId)
                .totalChanges(auditLogs.size())
                .auditLogs(responses)
                .summary(String.format("Found %d audit entries for %s", auditLogs.size(), entityType))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AuditLogResponse> getAuditTrail(AuditSearchRequest request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getSortDirection().toUpperCase());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(direction, request.getSortBy()));

        Page<AuditLog> page = auditRepository.searchAuditLogs(
                request.getEntityType(),
                request.getEntityId(),
                request.getUserId(),
                request.getAction(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        List<AuditLogResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<AuditLogResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .isFirst(page.isFirst())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> exportAuditLog(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> auditLogs = auditRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);

        return auditLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(UUID auditLogId) {
        return auditRepository.findById(auditLogId)
                .map(this::convertToResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getUserAuditHistory(UUID userId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = auditRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getActionHistory(AuditAction action) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = auditRepository.findByActionOrderByCreatedAtDesc(action, pageable);

        return page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCount() {
        return auditRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCountByEntity(UUID entityId) {
        return auditRepository.countByEntityId(entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCountByUser(UUID userId) {
        return auditRepository.countByUserId(userId);
    }

    private AuditLogResponse convertToResponse(AuditLog auditLog) {
        Map<String, Object> oldData = null;
        Map<String, Object> newData = null;
        Map<String, Map<String, Object>> changes = null;

        try {
            if (auditLog.getOldData() != null) {
                oldData = objectMapper.readValue(auditLog.getOldData(), Map.class);
            }
            if (auditLog.getNewData() != null) {
                newData = objectMapper.readValue(auditLog.getNewData(), Map.class);
            }
            if (auditLog.getChanges() != null) {
                changes = objectMapper.readValue(auditLog.getChanges(), Map.class);
            }
        } catch (Exception e) {
            log.warn("Error deserializing audit log data", e);
        }

        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .action(auditLog.getAction())
                .userId(auditLog.getUserId())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .description(auditLog.getDescription())
                .oldData(oldData)
                .newData(newData)
                .changes(changes)
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
