package com.platform.modules.menu.repository;

import com.platform.modules.menu.entity.MenuPermission;
import com.platform.modules.menu.entity.MenuPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuPermissionRepository extends JpaRepository<MenuPermission, MenuPermissionId> {

    @Query("SELECT mp FROM MenuPermission mp WHERE mp.menu.id = :menuId")
    List<MenuPermission> findByMenuId(@Param("menuId") UUID menuId);

    @Query("SELECT mp FROM MenuPermission mp WHERE mp.permission.id = :permissionId")
    List<MenuPermission> findByPermissionId(@Param("permissionId") UUID permissionId);

    @Query("SELECT CASE WHEN COUNT(mp) > 0 THEN true ELSE false END FROM MenuPermission mp WHERE mp.menu.id = :menuId AND mp.permission.id = :permissionId")
    boolean existsByMenuIdAndPermissionId(@Param("menuId") UUID menuId, @Param("permissionId") UUID permissionId);

    @Query("SELECT mp FROM MenuPermission mp WHERE mp.permission.roles.id = :roleId")
    List<MenuPermission> findByPermissionRoleId(@Param("roleId") UUID roleId);

    void deleteByMenuId(UUID menuId);

    void deleteByPermissionId(UUID permissionId);
}
