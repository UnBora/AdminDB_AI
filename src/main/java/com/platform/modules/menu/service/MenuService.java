package com.platform.modules.menu.service;

import com.platform.core.base.BaseService;
import com.platform.modules.menu.dto.request.MenuCreateRequest;
import com.platform.modules.menu.dto.request.MenuReorderRequest;
import com.platform.modules.menu.dto.request.MenuUpdateRequest;
import com.platform.modules.menu.dto.response.MenuResponse;
import com.platform.modules.menu.dto.response.MenuTreeResponse;
import com.platform.modules.menu.entity.Menu;

import java.util.List;
import java.util.UUID;

public interface MenuService extends BaseService<Menu, MenuResponse> {

    /**
     * Get all root-level menus (no parent)
     */
    List<MenuResponse> findAllRootMenus();

    /**
     * Get full hierarchical menu tree with all children
     */
    MenuTreeResponse findMenuTree();

    /**
     * Get menu tree for a specific role with permission filtering
     */
    List<MenuResponse> findVisibleMenusForRole(UUID roleId);

    /**
     * Create a new menu
     */
    MenuResponse createMenu(MenuCreateRequest request);

    /**
     * Update an existing menu
     */
    MenuResponse updateMenu(UUID id, MenuUpdateRequest request);

    /**
     * Soft delete a menu
     */
    void deleteMenu(UUID id);

    /**
     * Reorder multiple menus by updating their orderNumber
     */
    void reorderMenus(List<MenuReorderRequest> requests);

    /**
     * Rebuild the menu cache (usually called after bulk updates)
     */
    void rebuildMenuCache();

    /**
     * Get menu by ID with its children
     */
    MenuResponse getMenuWithChildren(UUID id);

    /**
     * Check if user has permission to access a menu
     */
    boolean hasMenuPermission(UUID userId, UUID menuId);
}
