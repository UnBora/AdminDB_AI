# Menu Service & Sidebar - Quick Reference

## 📋 Quick Start

### 1. Display Sidebar in Layout
```html
<!-- In your main layout file -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-lg-2">
                <div th:insert="fragments/sidebar :: sidebar-menu(menus=visibleMenus, currentPath=${#request.requestURI})"></div>
            </div>
            
            <!-- Main Content -->
            <div class="col-lg-10">
                <!-- Page content -->
            </div>
        </div>
    </div>
    
    <!-- Styles and Scripts -->
    <style th:insert="fragments/sidebar :: sidebar-styles"></style>
    <script th:insert="fragments/sidebar :: sidebar-script"></script>
</body>
</html>
```

### 2. Inject into All Views
The DashboardController automatically injects `visibleMenus` into all dashboard-related templates via `@ModelAttribute`. No additional controller changes needed.

### 3. Create a Menu Programmatically
```java
@Autowired
private MenuService menuService;

public void createMenuStructure() {
    // Create parent menu
    MenuResponse dashboard = menuService.createMenu(MenuCreateRequest.builder()
        .name("Dashboard")
        .translationKey("menu.dashboard")
        .url("/dashboard")
        .icon("bi-speedometer2")
        .orderNumber(1)
        .active(true)
        .build());
    
    // Create child menu
    MenuResponse analytics = menuService.createMenu(MenuCreateRequest.builder()
        .name("Analytics")
        .translationKey("menu.analytics")
        .url("/analytics")
        .icon("bi-bar-chart")
        .parentId(dashboard.getId())
        .orderNumber(1)
        .active(true)
        .build());
}
```

## 🎨 Common Tasks

### Get Menu Tree
```java
MenuTreeResponse tree = menuService.findMenuTree();
List<MenuResponse> allMenus = tree.getMenus();
System.out.println("Total menus: " + tree.getTotalCount());
System.out.println("Cache status: " + tree.getCacheStatus());
```

### Get Root Menus Only
```java
List<MenuResponse> rootMenus = menuService.findAllRootMenus();
```

### Get Role-Based Menus
```java
List<MenuResponse> roleMenus = menuService.findVisibleMenusForRole(roleId);
```

### Update Menu
```java
MenuResponse updated = menuService.updateMenu(menuId, MenuUpdateRequest.builder()
    .name("New Name")
    .icon("bi-new-icon")
    .orderNumber(5)
    .build());
```

### Delete Menu
```java
menuService.deleteMenu(menuId);  // Soft delete
```

### Reorder Multiple Menus
```java
List<MenuReorderRequest> reorders = Arrays.asList(
    MenuReorderRequest.builder().menuId(menu1Id).orderNumber(1).build(),
    MenuReorderRequest.builder().menuId(menu2Id).orderNumber(2).build(),
    MenuReorderRequest.builder().menuId(menu3Id).orderNumber(3).build()
);
menuService.reorderMenus(reorders);
```

### Rebuild Cache
```java
menuService.rebuildMenuCache();  // Called automatically on updates
```

## 🔍 Database Queries

### See all menus
```sql
SELECT id, name, parent_id, order_number, active FROM menus 
WHERE deleted = false 
ORDER BY order_number;
```

### Check menu permissions
```sql
SELECT m.name, p.name as permission 
FROM menus m
JOIN menu_permissions mp ON m.id = mp.menu_id
JOIN permissions p ON mp.permission_id = p.id
WHERE m.deleted = false;
```

## 📱 Sidebar Features

### Active Menu Highlighting
Automatically detects current page from URL path and highlights matching menu item. Parent menus auto-expand if a child is active.

### Mobile Responsive
- Desktop: Fixed sidebar (280px), visible
- Tablet: Collapsible sidebar
- Mobile: Hidden by default, toggle with hamburger menu

### Translations
Menu names use `translationKey` for i18n support. Requires translation keys in message properties files:
```properties
# messages_en.properties
menu.dashboard=Dashboard
menu.analytics=Analytics
menu.users=User Management
```

### Icons
Uses Bootstrap Icons (bi-*) classes:
- `bi-speedometer2` - Dashboard
- `bi-bar-chart` - Analytics
- `bi-people` - Users
- `bi-gear` - Settings

## 🎯 API Endpoints (Future)

When creating REST API endpoints for menu management:

```java
@RestController
@RequestMapping("/api/menus")
public class MenuAPIController {
    
    @GetMapping("/tree")
    public MenuTreeResponse getTree() {
        return menuService.findMenuTree();
    }
    
    @GetMapping("/role/{roleId}")
    public List<MenuResponse> getByRole(@PathVariable UUID roleId) {
        return menuService.findVisibleMenusForRole(roleId);
    }
    
    @PostMapping
    public MenuResponse create(@Valid @RequestBody MenuCreateRequest request) {
        return menuService.createMenu(request);
    }
    
    @PutMapping("/{id}")
    public MenuResponse update(@PathVariable UUID id, @Valid @RequestBody MenuUpdateRequest request) {
        return menuService.updateMenu(id, request);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        menuService.deleteMenu(id);
    }
}
```

## 🐛 Troubleshooting

### Menu Not Appearing
1. Check menu is `active = true` and `deleted = false`
2. Verify translation key exists in messages properties
3. Clear cache: `menuService.rebuildMenuCache()`

### Parent Not Expanding for Child
1. Ensure child's `parentId` matches parent's `id`
2. Check child menu URL matches current page path
3. Verify JavaScript is loaded (check browser console)

### Sidebar Styling Issues
1. Verify Bootstrap 5.x is included before sidebar script
2. Check Bootstrap icon font is loaded (CDN or local)
3. Ensure CSS fragment is inserted in template

### Performance Issues
1. Call `rebuildMenuCache()` after bulk updates
2. Use `findVisibleMenusForRole()` instead of tree if role is known
3. Check database indexes on parent_id and order_number

## 📚 Related Documentation

- [Menu Service Implementation](MENU_SERVICE_IMPLEMENTATION.md) - Full technical details
- [Database Schema](DEPLOYMENT_GUIDE.md) - Menu table structure
- [Security Model](USER_MANAGEMENT_API_DOCUMENTATION.md) - Role-based permissions

## 🔄 Cache Information

**Cache Name**: `menus_root`  
**In-Memory Cache Key**: `menu_tree`  
**Invalidation**: Automatic on create/update/delete/reorder  
**Manual Rebuild**: `menuService.rebuildMenuCache()`

## 📝 Translation Keys Template

Add these to your `messages.properties` files:

```properties
# English (messages_en.properties)
menu.dashboard=Dashboard
menu.dashboard.overview=Dashboard Overview
menu.analytics=Analytics
menu.analytics.reports=Reports
menu.users=User Management
menu.users.list=User List
menu.settings=Settings
sidebar.no-menus=No menus available

# Spanish (messages_es.properties)
menu.dashboard=Panel de Control
menu.analytics=Análisis
menu.users=Gestión de Usuarios
menu.settings=Configuración
```

---
**Version**: 1.0  
**Last Updated**: 2024-05-25  
**Status**: Production Ready ✓
