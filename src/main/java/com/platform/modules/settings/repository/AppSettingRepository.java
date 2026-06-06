package com.platform.modules.settings.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.settings.entity.AppSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppSettingRepository extends BaseRepository<AppSetting> {
    Optional<AppSetting> findBySettingKey(String settingKey);

    List<AppSetting> findByCategory(String category);

    @Query("SELECT a FROM AppSetting a WHERE a.category = :category AND a.deleted = false")
    List<AppSetting> findActiveByCategoryAndNotDeleted(@Param("category") String category);

    @Query("SELECT a FROM AppSetting a WHERE a.deleted = false")
    List<AppSetting> findAllActive();

    boolean existsBySettingKey(String settingKey);
}
