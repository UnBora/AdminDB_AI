package com.platform.modules.audit.repository;

import com.platform.modules.audit.entity.AuditAction;
import com.platform.modules.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntityIdOrderByCreatedAtDesc(UUID entityId);

    List<AuditLog> findByEntityIdAndActionOrderByCreatedAtDesc(UUID entityId, AuditAction action);

    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    Page<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);

    Page<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> searchAuditLogs(
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId,
            @Param("userId") UUID userId,
            @Param("action") AuditAction action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    long countByEntityId(UUID entityId);

    long countByUserId(UUID userId);

    long countByAction(AuditAction action);
}
