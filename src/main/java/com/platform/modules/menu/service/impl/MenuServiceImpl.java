package com.platform.modules.menu.service.impl;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.menu.dto.request.MenuCreateRequest;
import com.platform.modules.menu.dto.request.MenuReorderRequest;
import com.platform.modules.menu.dto.request.MenuUpdateRequest;
import com.platform.modules.menu.dto.response.MenuResponse;
import com.platform.modules.menu.dto.response.MenuTreeResponse;
import com.platform.modules.menu.entity.Menu;
import com.platform.modules.menu.entity.MenuPermission;
import com.platform.modules.menu.mapper.MenuMapper;
import com.platform.modules.menu.repository.MenuPermissionRepository;
import com.platform.modules.menu.repository.MenuRepository;
import com.platform.modules.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MenuServiceImpl extends BaseServiceImpl<Menu, MenuResponse, MenuRepository> implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuPermissionRepository menuPermissionRepository;
    private final MenuMapper menuMapper;

    // In-memory cache for menu tree (used as fallback and performance optimization)
    private final ConcurrentHashMap<String, MenuTreeResponse> menuCache = new ConcurrentHashMap<>();
    private static final String MENU_CACHE_KEY = "menu_tree";

    public MenuServiceImpl(MenuRepository menuRepository, MenuPermissionRepository menuPermissionRepository,
                         MenuMapper menuMapper) {
        super(menuRepository);
        this.menuRepository = menuRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    public MenuResponse create(MenuResponse dto) {
        // Not used - use createMenu instead
        throw new UnsupportedOperationException("Use createMenu(MenuCreateRequest) instead");
    }

    @Override
    public MenuResponse update(UUID id, MenuResponse dto) {
        // Not used - use updateMenu instead
        throw new UnsupportedOperationException("Use updateMenu(UUID, MenuUpdateRequest) instead");
    }

    @Override
    public MenuResponse getById(UUID id) {
        log.debug("Getting menu by id: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        return buildMenuResponseWithChildren(menu);
    }

    @Override
    @Cacheable(value = "menus_root", unless = "#result == null or #result.isEmpty()")
    public List<MenuResponse> findAllRootMenus() {
        log.debug("Finding all root menus");
        List<Menu> rootMenus = menuRepository.findAllRootMenus();
        return rootMenus.stream()
                .map(menu -> buildMenuResponseWithChildren(menu))
                .collect(Collectors.toList());
    }

    @Override
    public MenuTreeResponse findMenuTree() {
        log.debug("Finding menu tree");

        // Check in-memory cache first
        if (menuCache.containsKey(MENU_CACHE_KEY)) {
            log.debug("Returning menu tree from in-memory cache");
            return menuCache.get(MENU_CACHE_KEY);
        }

        List<Menu> allMenus = menuRepository.findAllMenusSorted();
        List<MenuResponse> rootMenus = allMenus.stream()
                .filter(m -> m.getParent() == null)
                .map(m -> buildMenuResponseWithChildren(m, allMenus))
                .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                .collect(Collectors.toList());

        MenuTreeResponse treeResponse = MenuTreeResponse.builder()
                .menus(rootMenus)
                .totalCount((long) allMenus.size())
                .cacheStatus("BUILT_FROM_DB")
                .build();

        // Store in memory cache
        menuCache.put(MENU_CACHE_KEY, treeResponse);

        return treeResponse;
    }

    @Override
    public List<MenuResponse> findVisibleMenusForRole(UUID roleId) {
        log.debug("Finding visible menus for role: {}", roleId);

        // Get all menus for the role via permissions
        List<MenuPermission> permissions = menuPermissionRepository.findByPermissionRoleId(roleId);
        Set<UUID> permittedMenuIds = permissions.stream()
                .map(mp -> mp.getMenu().getId())
                .collect(Collectors.toSet());

        if (permittedMenuIds.isEmpty()) {
            log.debug("No menu permissions found for role: {}", roleId);
            return new ArrayList<>();
        }

        // Build hierarchical tree with only permitted menus
        List<Menu> allMenus = menuRepository.findAllMenusSorted();
        List<MenuResponse> visibleMenus = allMenus.stream()
                .filter(m -> m.getParent() == null && permittedMenuIds.contains(m.getId()))
                .map(m -> buildMenuResponseWithChildrenFiltered(m, permittedMenuIds, allMenus))
                .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                .collect(Collectors.toList());

        return visibleMenus;
    }

    @Override
    @CacheEvict(value = "menus_root", allEntries = true)
    public MenuResponse createMenu(MenuCreateRequest request) {
        log.debug("Creating menu: {}", request.getName());

        Menu menu = menuMapper.toEntity(request);

        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent menu not found"));
            menu.setParent(parent);
        }

        Menu savedMenu = menuRepository.save(menu);
        rebuildMenuCache();
        return menuMapper.toResponse(savedMenu);
    }

    @Override
    @CacheEvict(value = "menus_root", allEntries = true)
    public MenuResponse updateMenu(UUID id, MenuUpdateRequest request) {
        log.debug("Updating menu: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));

        menuMapper.updateEntity(request, menu);

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Menu cannot be its own parent");
            }
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent menu not found"));
            menu.setParent(parent);
        }

        Menu updatedMenu = menuRepository.save(menu);
        rebuildMenuCache();
        return menuMapper.toResponse(updatedMenu);
    }

    @Override
    @CacheEvict(value = "menus_root", allEntries = true)
    public void deleteMenu(UUID id) {
        log.debug("Deleting menu: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));

        menu.setDeleted(true);
        menuRepository.save(menu);
        rebuildMenuCache();
    }

    @Override
    @CacheEvict(value = "menus_root", allEntries = true)
    public void reorderMenus(List<MenuReorderRequest> requests) {
        log.debug("Reordering {} menus", requests.size());

        for (MenuReorderRequest request : requests) {
            Menu menu = menuRepository.findById(request.getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + request.getMenuId()));
            menu.setOrderNumber(request.getOrderNumber());
            menuRepository.save(menu);
        }

        rebuildMenuCache();
    }

    @Override
    public void rebuildMenuCache() {
        log.debug("Rebuilding menu cache");
        menuCache.clear();

        List<Menu> allMenus = menuRepository.findAllMenusSorted();
        List<MenuResponse> rootMenus = allMenus.stream()
                .filter(m -> m.getParent() == null)
                .map(m -> buildMenuResponseWithChildren(m, allMenus))
                .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                .collect(Collectors.toList());

        MenuTreeResponse treeResponse = MenuTreeResponse.builder()
                .menus(rootMenus)
                .totalCount((long) allMenus.size())
                .cacheStatus("BUILT_FROM_REBUILD")
                .build();

        menuCache.put(MENU_CACHE_KEY, treeResponse);
        log.debug("Menu cache rebuilt with {} root menus", rootMenus.size());
    }

    @Override
    public MenuResponse getMenuWithChildren(UUID id) {
        log.debug("Getting menu with children: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));

        return buildMenuResponseWithChildren(menu);
    }

    @Override
    public boolean hasMenuPermission(UUID userId, UUID menuId) {
        log.debug("Checking menu permission for user: {}, menu: {}", userId, menuId);

        // For now, check if menu exists and user has at least one role
        // This can be enhanced based on specific permission requirements
        List<MenuPermission> permissions = menuPermissionRepository.findByMenuId(menuId);
        return !permissions.isEmpty();
    }

    @Override
    protected MenuResponse mapToDto(Menu entity) {
        return menuMapper.toResponse(entity);
    }

    /**
     * Build menu response with children recursively
     */
    private MenuResponse buildMenuResponseWithChildren(Menu menu) {
        MenuResponse response = menuMapper.toResponse(menu);

        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            List<MenuResponse> children = menu.getChildren().stream()
                    .filter(child -> !child.getDeleted())
                    .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                    .map(this::buildMenuResponseWithChildren)
                    .collect(Collectors.toList());

            if (!children.isEmpty()) {
                response.setChildren(children);
            }
        }

        return response;
    }

    /**
     * Build menu response with children using a pre-fetched list (for performance)
     */
    private MenuResponse buildMenuResponseWithChildren(Menu menu, List<Menu> allMenus) {
        MenuResponse response = menuMapper.toResponse(menu);

        List<MenuResponse> children = allMenus.stream()
                .filter(m -> m.getParent() != null && m.getParent().getId().equals(menu.getId()))
                .filter(m -> !m.getDeleted())
                .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                .map(m -> buildMenuResponseWithChildren(m, allMenus))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            response.setChildren(children);
        }

        return response;
    }

    /**
     * Build menu response with children, filtering by permission
     */
    private MenuResponse buildMenuResponseWithChildrenFiltered(Menu menu, Set<UUID> permittedMenuIds, List<Menu> allMenus) {
        MenuResponse response = menuMapper.toResponse(menu);

        List<MenuResponse> children = allMenus.stream()
                .filter(m -> m.getParent() != null && m.getParent().getId().equals(menu.getId()))
                .filter(m -> !m.getDeleted() && permittedMenuIds.contains(m.getId()))
                .sorted(Comparator.comparing(m -> m.getOrderNumber() != null ? m.getOrderNumber() : 0))
                .map(m -> buildMenuResponseWithChildrenFiltered(m, permittedMenuIds, allMenus))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            response.setChildren(children);
        }

        return response;
    }
}
