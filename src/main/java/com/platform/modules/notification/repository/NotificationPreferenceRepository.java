package com.platform.modules.notification.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.notification.entity.NotificationPreference;
import com.platform.modules.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferenceRepository extends BaseRepository<NotificationPreference> {

    @Query("SELECT np FROM NotificationPreference np WHERE np.user.id = :userId AND np.notificationType = :type AND np.deleted = false")
    Optional<NotificationPreference> findByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") NotificationType type
    );

    @Query("SELECT np FROM NotificationPreference np WHERE np.user.id = :userId AND np.deleted = false")
    List<NotificationPreference> findByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM NotificationPreference np WHERE np.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
