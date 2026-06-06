package com.platform.modules.dashboard.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.dashboard.entity.DashboardWidget;
import com.platform.modules.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardWidgetRepository extends BaseRepository<DashboardWidget> {
    
    List<DashboardWidget> findByUserAndDeletedFalseOrderByPositionAsc(User user);
    
    List<DashboardWidget> findByUserAndVisibleTrueAndDeletedFalseOrderByPositionAsc(User user);
}
