package com.platform.modules.notification.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<Notification> {

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadFalse(UUID userId, Pageable pageable);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.read = false AND n.deleted = false")
    long countUnreadByUserId(@Param("userId") UUID userId);

    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsReadById(@Param("id") UUID id);

    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredNotifications();

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
