# Quick Reference Guide

## 🚀 Quick Start (5 Minutes)

### 1. Database Setup
```bash
# Create PostgreSQL database
createdb admin_db

# Database will be auto-initialized via Liquibase on startup
```

### 2. Build & Run
```bash
cd /Users/macbookair/Desktop/AdminDB_AI

# Build
mvn clean install -DskipTests

# Run
mvn spring-boot:run

# Access: http://localhost:8080
```

### 3. Login
- Username: `admin`
- Password: `admin123`

## 📂 Adding a New Module

### Step 1: Create Entity
```java
@Entity
@Table(name = "your_table")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class YourEntity extends BaseEntity {
    @Column(name = "name")
    private String name;
}
```

### Step 2: Create Repository
```java
@Repository
public interface YourRepository extends BaseRepository<YourEntity> {
    Optional<YourEntity> findByName(String name);
}
```

### Step 3: Create DTOs
```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class YourRequest { }

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class YourResponse { }
```

### Step 4: Create Mapper
```java
@Mapper(componentModel = "spring")
public interface YourMapper {
    YourEntity toEntity(YourRequest request);
    YourResponse toResponse(YourEntity entity);
}
```

### Step 5: Create Service
```java
public interface YourService extends BaseService<YourEntity, YourResponse> {
    // Custom methods here
}

@Service @Transactional @RequiredArgsConstructor
public class YourServiceImpl extends BaseServiceImpl<YourEntity, YourResponse, YourRepository> 
    implements YourService {
    
    private final YourRepository repository;
    private final YourMapper mapper;
    
    @Override
    public YourResponse create(YourResponse dto) { }
    
    @Override
    protected YourResponse mapToDto(YourEntity entity) {
        return mapper.toResponse(entity);
    }
}
```

### Step 6: Create Controller
```java
@RestController
@RequestMapping("/api/your-entity")
@RequiredArgsConstructor
public class YourController extends BaseController<YourResponse, YourService> {
    // Inherits: POST, PUT, DELETE, GET (id), GET (paginated)
}
```

### Step 7: Add Liquibase Migration
```yaml
databaseChangeLog:
  - changeSet:
      id: vXXX_create_your_table
      author: platform
      changes:
        - createTable:
            tableName: your_table
            columns:
              - column:
                  name: id
                  type: uuid
                  constraints:
                    primaryKey: true
```

## 🔑 Key Classes to Know

| Class | Purpose |
|-------|---------|
| `BaseEntity` | All entities extend this - provides UUID, audit fields, soft delete |
| `BaseRepository` | All repos extend this - provides pagination, specifications, CRUD |
| `BaseService` | All services implement this - defines CRUD contract |
| `BaseServiceImpl` | Provides default CRUD implementation |
| `BaseController` | All controllers extend this - provides REST endpoints |
| `ApiResponse` | Wraps all API responses for consistency |
| `TranslationService` | Get translations: `translationService.get("key")` |

## 🗄️ Common Database Queries

```sql
-- Find all users with their roles
SELECT u.*, r.name as role_name 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
LEFT JOIN roles r ON ur.role_id = r.id 
WHERE u.deleted = false;

-- Get all permissions for a role
SELECT p.* 
FROM permissions p 
JOIN role_permissions rp ON p.id = rp.permission_id 
WHERE rp.role_id = ?;

-- View audit trail
SELECT * FROM audit_logs 
WHERE entity_type = 'User' 
ORDER BY created_at DESC 
LIMIT 20;

-- Get all translations for a locale
SELECT * FROM translations 
WHERE locale = 'en' AND deleted = false;
```

## 🎨 Creating Thymeleaf Templates

### Main Layout Fragment
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/main-layout}">
<head>
    <title>Your Page</title>
</head>
<body>
    <div layout:fragment="content">
        <h1>Your Content Here</h1>
    </div>
</body>
</html>
```

### Using Translations
```html
<button th:text="${@translationService.get('button.save')}">Save</button>
<label th:text="${@translationService.get('label.email')}">Email</label>
```

### Pagination
```html
<a th:href="@{/api/users(pageNumber=${pagination.pageNumber + 1})}"
   th:if="${pagination.hasNext}">
   Next Page
</a>
```

## 🔒 Security Annotations

```java
// Only ADMIN role
@PreAuthorize("hasRole('ADMIN')")

// Only ADMIN role
@PreAuthorize("hasAuthority('ROLE_ADMIN')")

// Specific permission
@PreAuthorize("hasAuthority('USER_CREATE')")

// Multiple permissions (OR)
@PreAuthorize("hasAuthority('USER_EDIT') or hasAuthority('ADMIN_ACCESS')")

// Custom expression
@PreAuthorize("@permissionEvaluator.canEdit(#id, authentication)")
```

## 📊 API Response Examples

### Success Response
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "username": "john_doe",
    "email": "john@example.com"
  },
  "statusCode": 200,
  "timestamp": "2024-05-23T16:03:28Z"
}
```

### Error Response
```json
{
  "success": false,
  "message": "User not found with id: invalid-id",
  "data": null,
  "statusCode": 404,
  "timestamp": "2024-05-23T16:03:28Z"
}
```

### Pagination Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "content": [ ... ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "statusCode": 200
}
```

## 🐛 Common Issues & Solutions

### Issue: Port 8080 already in use
```bash
# Change port in application.yml
server:
  port: 8081
```

### Issue: PostgreSQL connection refused
```bash
# Verify PostgreSQL is running
brew services list  # macOS
psql -U postgres   # Test connection
```

### Issue: Liquibase migration fails
```bash
# Check changelog file names match master changelog
# Ensure YAML format is correct
# Verify foreign key constraints are correct
```

### Issue: MapStruct not generating implementation
```bash
# Add to pom.xml annotationProcessorPaths:
<path>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct-processor</artifactId>
  <version>${mapstruct.version}</version>
</path>
```

## 📝 Useful Maven Commands

```bash
# Build without tests
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Generate dependencies report
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates

# Run specific test
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

## 🚀 Deployment Checklist

- [ ] Update `application-prod.yml` with production settings
- [ ] Set environment variables for database credentials
- [ ] Run `mvn clean package -DskipTests`
- [ ] Test with `java -jar target/*.jar --spring.profiles.active=prod`
- [ ] Verify database migrations completed
- [ ] Test login with production credentials
- [ ] Set up monitoring and logging
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
- [ ] Document deployment procedure

## 📚 Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Thymeleaf: https://www.thymeleaf.org/
- MapStruct: https://mapstruct.org/
- Liquibase: https://www.liquibase.org/

---

**Version**: 1.0.0
**Last Updated**: May 23, 2024
