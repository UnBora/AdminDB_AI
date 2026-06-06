# Module Development Template

This is a step-by-step template for adding new modules to the admin dashboard platform.

## Complete Module Example: User Module (Already Implemented)

### 1. Entity Class

File: `src/main/java/com/platform/modules/user/entity/User.java`

```java
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User extends BaseEntity {
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    // Relationships
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

### 2. Repository Interface

File: `src/main/java/com/platform/modules/user/repository/UserRepository.java`

```java
@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### 3. Request DTOs

File: `src/main/java/com/platform/modules/user/dto/request/UserCreateRequest.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserCreateRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;
    
    private String firstName;
    private String lastName;
    private Set<UUID> roleIds;
}
```

### 4. Response DTOs

File: `src/main/java/com/platform/modules/user/dto/response/UserResponse.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
    private Set<RoleResponse> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 5. Mapper (MapStruct)

File: `src/main/java/com/platform/modules/user/mapper/UserMapper.java`

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserCreateRequest request);
    
    UserResponse toResponse(User entity);
    
    RoleResponse toResponse(Role entity);
}
```

### 6. Service Interface

File: `src/main/java/com/platform/modules/user/service/UserService.java`

```java
public interface UserService extends BaseService<User, UserResponse> {
    
    UserResponse create(UserCreateRequest request);
    
    UserResponse update(UUID id, UserUpdateRequest request);
    
    UserResponse getByUsername(String username);
    
    UserResponse assignRole(UUID userId, UUID roleId);
}
```

### 7. Service Implementation

File: `src/main/java/com/platform/modules/user/service/impl/UserServiceImpl.java`

```java
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<User, UserResponse, UserRepository> 
    implements UserService {
    
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse create(UserCreateRequest request) {
        // Check if username exists
        if (repository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = repository.save(user);
        return mapper.toResponse(savedUser);
    }
    
    @Override
    protected UserResponse mapToDto(User entity) {
        return mapper.toResponse(entity);
    }
}
```

### 8. REST Controller

File: `src/main/java/com/platform/modules/user/controller/UserController.java`

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController<UserResponse, UserService> {
    
    private final UserService userService;
    
    // Inherits from BaseController:
    // - POST /api/users (create)
    // - PUT /api/users/{id} (update)
    // - DELETE /api/users/{id} (delete)
    // - GET /api/users/{id} (getById)
    // - GET /api/users (paginated list)
    
    // Add custom endpoints below:
    
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        UserResponse result = userService.assignRole(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
```

### 9. Liquibase Migration

File: `src/main/resources/db/changelog/vXXX_create_users_table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: vXXX_create_users_table
      author: platform
      changes:
        - createTable:
            tableName: users
            columns:
              - column:
                  name: id
                  type: uuid
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: username
                  type: varchar(100)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: email
                  type: varchar(255)
                  constraints:
                    nullable: false
                    unique: true
              # ... more columns
        
        - createIndex:
            tableName: users
            indexName: idx_users_username
            columns:
              - column:
                  name: username
```

### 10. Thymeleaf Template

File: `src/main/resources/templates/pages/users/list.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/main-layout}">
<head>
    <title>Users</title>
</head>
<body>
    <div layout:fragment="content">
        <div class="container-fluid">
            <h1>User Management</h1>
            
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#userModal">
                <i class="fas fa-plus"></i> New User
            </button>
            
            <table class="table table-striped" id="usersTable">
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Roles</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="usersTableBody">
                    <!-- DataTable will populate -->
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
```

## Quick Module Checklist

When adding a new module, create:

- [ ] Entity class extending BaseEntity
- [ ] Repository interface extending BaseRepository
- [ ] Service interface extending BaseService
- [ ] Service implementation extending BaseServiceImpl
- [ ] Request DTOs with validation
- [ ] Response DTOs
- [ ] Mapper interface (MapStruct)
- [ ] Controller extending BaseController
- [ ] Liquibase migration(s)
- [ ] Thymeleaf templates
- [ ] Unit tests
- [ ] Integration tests

## Expected Code Lines per Module

| Component | Lines | Notes |
|-----------|-------|-------|
| Entity | 50-100 | Depends on relationships |
| Repository | 30-50 | Custom query methods |
| Service Interface | 40-60 | Method signatures |
| Service Implementation | 100-200 | Business logic |
| Controller | 80-150 | REST endpoints |
| Mappers | 50-80 | DTO conversions |
| DTOs (all) | 100-200 | Request + Response |
| Tests | 200-400 | Unit + Integration |
| **Total** | **700-1200** | Per module |

## API Endpoints Created Automatically

```
POST   /api/resource              - Create new resource
GET    /api/resource/{id}         - Get by ID
PUT    /api/resource/{id}         - Update resource
DELETE /api/resource/{id}         - Delete resource (soft delete)
GET    /api/resource              - List with pagination
```

Query parameters for listing:
```
GET /api/resource?pageNumber=0&pageSize=10&sortBy=createdAt&sortDirection=desc
```

## Response Format

All responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* entity data */ },
  "statusCode": 200,
  "timestamp": "2024-05-23T16:03:28Z"
}
```

## Naming Conventions

- **Packages**: `com.platform.modules.{moduleName}`
- **Entities**: `{EntityName}` (User, Product, Order)
- **Repositories**: `{EntityName}Repository`
- **Services**: `{EntityName}Service` interface, `{EntityName}ServiceImpl` impl
- **Controllers**: `{EntityName}Controller`
- **DTOs**: `{EntityName}CreateRequest`, `{EntityName}UpdateRequest`, `{EntityName}Response`
- **Mappers**: `{EntityName}Mapper`
- **Tables**: `{table_name}` (lowercase with underscores)
- **Migrations**: `vXXX_{description}.yaml` (v001, v002, etc.)

## Example: Creating a Product Module

1. Create Entity:
```java
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
```

2. Create Repository:
```java
@Repository
public interface ProductRepository extends BaseRepository<Product> {
    List<Product> findByNameContaining(String name);
}
```

3. Follow the same pattern for other components...

That's it! You'll have full CRUD API with:
- ✅ Pagination
- ✅ Sorting
- ✅ Validation
- ✅ Error handling
- ✅ Type-safe mapping
- ✅ Transaction management
- ✅ Audit trail

---

**Using this template, you can create a complete module in 30-60 minutes!**
