package com.platform.modules.portfolio.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.portfolio.entity.Portfolio;
import com.platform.modules.project.entity.Project;
import com.platform.modules.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends BaseRepository<Portfolio> {
    
    List<Portfolio> findByOwnerAndDeletedFalse(User owner);
    
    List<Portfolio> findByProjectAndDeletedFalse(Project project);
}
