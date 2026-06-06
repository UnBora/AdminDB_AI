package com.platform.modules.project.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.project.entity.Project;
import com.platform.modules.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends BaseRepository<Project> {
    
    List<Project> findByOwnerAndDeletedFalse(User owner);
    
    List<Project> findByStatusAndDeletedFalse(String status);
}
