package com.platform.modules.user.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.user.entity.Permission;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission> {
    Optional<Permission> findByName(String name);
}
