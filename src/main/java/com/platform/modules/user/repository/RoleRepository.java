package com.platform.modules.user.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.user.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {
    Optional<Role> findByName(String name);
}
