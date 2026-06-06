# User Service Enhanced API Reference

## Quick Start

### Import Required Classes
```java
import com.platform.modules.user.model.FilterCriteria;
import com.platform.modules.user.model.FilterCriteria.FilterOperation;
import com.platform.modules.user.specification.UserSpecification;
```

## Search Operations

### 1. Simple Email Search
```java
PaginationResponse<UserResponse> results = userService.searchByEmail(
    "example@",  // Partial email search
    0,           // Page number
    20,          // Page size
    "createdAt", // Sort by
    "desc"       // Sort direction
);
```

### 2. Simple Username Search
```java
PaginationResponse<UserResponse> results = userService.searchByUsername(
    "john",      // Partial username search
    0,           // Page number
    20,          // Page size
    "username",  // Sort by
    "asc"        // Sort direction
);
```

### 3. Advanced Dynamic Filtering
```java
List<FilterCriteria> filters = Arrays.asList(
    FilterCriteria.builder()
        .filterKey("active")
        .operation(FilterOperation.EQUAL)
        .value(true)
        .build(),
    FilterCriteria.builder()
        .filterKey("email")
        .operation(FilterOperation.LIKE)
        .value("@company.com")
        .build()
);

PaginationResponse<UserResponse> results = userService.searchWithFilters(
    filters,
    0,           // Page number
    20,          // Page size
    "createdAt", // Sort by
    "desc"       // Sort direction
);
```

### 4. Filter by Active Status
```java
PaginationResponse<UserResponse> activeUsers = userService.searchByActiveStatus(
    true,        // Active status
    0,
    50,
    "username",
    "asc"
);
```

### 5. Filter by Date Range
```java
LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59);

PaginationResponse<UserResponse> newUsers = userService.searchByCreatedDateRange(
    startDate,
    endDate,
    0,
    100,
    "createdAt",
    "desc"
);
```

## Role Management

### 1. Assign Single Role
```java
UserResponse updatedUser = userService.assignRole(userId, roleId);
```

### 2. Remove Single Role
```java
UserResponse updatedUser = userService.removeRole(userId, roleId);
```

### 3. Assign Multiple Roles (Batch)
```java
Set<UUID> roleIds = Set.of(roleId1, roleId2, roleId3);
UserResponse updatedUser = userService.assignRoles(userId, roleIds);
```

### 4. Remove Multiple Roles (Batch)
```java
Set<UUID> roleIds = Set.of(roleId1, roleId2);
UserResponse updatedUser = userService.removeRoles(userId, roleIds);
```

### 5. Replace All Roles (Atomic)
```java
Set<UUID> newRoleIds = Set.of(roleId4, roleId5);
UserResponse updatedUser = userService.changeRoles(userId, newRoleIds);
```

## Batch Operations

### 1. Batch Update Active Status
```java
List<UUID> userIds = Arrays.asList(userId1, userId2, userId3);
userService.batchUpdateActiveStatus(userIds, true); // Activate users
```

### 2. Export Users
```java
List<FilterCriteria> filters = Arrays.asList(
    FilterCriteria.builder()
        .filterKey("active")
        .operation(FilterOperation.EQUAL)
        .value(true)
        .build()
);

List<UserResponse> exportedUsers = userService.exportUsers(filters);
// Use for CSV/Excel export
```

## Analytics

### 1. Get Active User Count
```java
long activeCount = userService.getActiveUserCount();
```

### 2. Get Inactive User Count
```java
long inactiveCount = userService.getInactiveUserCount();
```

## Validation

### 1. Validate User Creation
```java
UserCreateRequest request = UserCreateRequest.builder()
    .username("john_doe")
    .email("john@example.com")
    .password("SecurePass123!")
    .firstName("John")
    .lastName("Doe")
    .build();

UserValidator.ValidationResult result = validator.validateUserCreate(request);
if (!result.isValid()) {
    result.getErrors().forEach(error -> 
        System.out.println(error.getFieldName() + ": " + error.getMessage())
    );
}
```

### 2. Validate User Update
```java
UserUpdateRequest request = UserUpdateRequest.builder()
    .email("newemail@example.com")
    .build();

UserValidator.ValidationResult result = validator.validateUserUpdate(request, userId);
if (result.hasErrors()) {
    // Handle validation errors
}
```

## Filter Operations Reference

| Operation | Usage | Example |
|-----------|-------|---------|
| EQUAL | Exact match | `active = true` |
| NOT_EQUAL | Not equal | `active != false` |
| GREATER_THAN | Greater than | `createdAt > date` |
| LESS_THAN | Less than | `createdAt < date` |
| GREATER_THAN_OR_EQUAL | >= | `createdAt >= date` |
| LESS_THAN_OR_EQUAL | <= | `createdAt <= date` |
| LIKE | Partial match | `username LIKE '%john%'` |
| IN | In collection | `id IN (id1, id2, id3)` |
| BETWEEN | Range | `createdAt BETWEEN start AND end` |
| IS_NULL | Null check | `deletedAt IS NULL` |
| IS_NOT_NULL | Not null | `deletedAt IS NOT NULL` |

## Error Handling

### ValidationResult Error Structure
```java
public class ValidationError {
    private String fieldName;    // e.g., "email", "password"
    private String message;      // e.g., "Email already exists"
}
```

### Common Exceptions
- `BusinessException` - Validation or business rule violation
- `ResourceNotFoundException` - User/Role not found

### Example Error Handling
```java
try {
    UserResponse user = userService.create(request);
} catch (BusinessException e) {
    // Handle validation failure
    logger.error("User creation validation failed: {}", e.getMessage());
} catch (ResourceNotFoundException e) {
    // Handle resource not found
    logger.error("Resource not found: {}", e.getMessage());
}
```

## Response Format

### PaginationResponse Structure
```json
{
    "content": [...],           // List of UserResponse objects
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 150,
    "totalPages": 8,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
}
```

## Soft Delete

```java
// Soft delete preserves data with logical deletion flag
userService.softDelete(userId);

// Deleted users are automatically filtered from standard queries
// To include deleted users, use raw repository methods
```

## Best Practices

1. **Use searchWithFilters() for Complex Queries** - More flexible than specific methods
2. **Combine Filters Efficiently** - Fewer database hits when possible
3. **Validate Before Creating** - Always validate user input
4. **Use Batch Operations** - More efficient than individual operations
5. **Handle ValidationResult Properly** - Provide detailed feedback to UI
6. **Respect Pagination** - Avoid loading all records at once
7. **Use Transaction Management** - Batch operations are transactional
8. **Check Export Size** - Limit exported records for memory efficiency

