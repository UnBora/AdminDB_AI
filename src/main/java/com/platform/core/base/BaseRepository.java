package com.platform.core.base;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    List<T> findByDeletedFalse();

    List<T> findByIdAndDeletedFalse(UUID id);
}
