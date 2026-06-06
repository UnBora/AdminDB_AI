package com.platform.modules.menu.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.menu.entity.Menu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends BaseRepository<Menu> {

    @Query("SELECT m FROM Menu m WHERE m.parent IS NULL AND m.deleted = false ORDER BY m.orderNumber ASC")
    List<Menu> findAllRootMenus();

    @Query("SELECT m FROM Menu m WHERE m.parent.id = :parentId AND m.deleted = false ORDER BY m.orderNumber ASC")
    List<Menu> findChildrenByParentId(@Param("parentId") UUID parentId);

    @Query("SELECT m FROM Menu m WHERE m.deleted = false ORDER BY m.orderNumber ASC")
    List<Menu> findAllMenusSorted();

    @Query("SELECT m FROM Menu m WHERE m.active = true AND m.deleted = false ORDER BY m.orderNumber ASC")
    List<Menu> findAllActiveMenus();
}
