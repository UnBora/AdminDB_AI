# Admin Dashboard Platform - Developer Setup Guide

## Project Overview

The Admin Dashboard Platform is a reusable, enterprise-level SaaS admin dashboard built with:
- **Backend**: Spring Boot 3.3, Spring Security, Spring Data JPA, Hibernate
- **Database**: PostgreSQL with Liquibase migrations
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js, DataTables
- **Build**: Maven 3.8+, Java 21

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 21**
   ```bash
   # macOS
   brew install openjdk@21
   
   # Ubuntu/Debian
   sudo apt-get install openjdk-21-jdk
   
   # Verify installation
   java -version
   ```

2. **Apache Maven 3.8+**
   ```bash
   # macOS
   brew install maven
   
   # Ubuntu/Debian
   sudo apt-get install maven
   
   # Verify installation
   mvn -version
   ```

3. **PostgreSQL 14+**
   ```bash
   # macOS
   brew install postgresql@14
   
   # Ubuntu/Debian
   sudo apt-get install postgresql postgresql-contrib
   
   # Verify installation
   psql --version
   ```

4. **Git**
   ```bash
   # macOS
   brew install git
   
   # Ubuntu/Debian
   sudo apt-get install git
   
   # Verify installation
   git --version
   ```

### Recommended Tools

- **IDE**: IntelliJ IDEA Community Edition or VS Code
- **Git GUI**: GitHub Desktop or GitKraken
- **Database GUI**: DBeaver or pgAdmin
- **API Client**: Postman or Insomnia
- **Docker**: For containerized development

## Project Setup

### 1. Clone Repository
```bash
git clone https://github.com/your-org/admin-dashboard-platform.git
cd admin-dashboard-platform
```

### 2. Create PostgreSQL Database and User
```bash
# Connect to PostgreSQL
psql -U postgres

# Execute in psql
CREATE DATABASE admin_dashboard_dev 
  WITH OWNER postgres 
  ENCODING 'UTF8' 
  LC_COLLATE = 'en_US.UTF-8' 
  LC_CTYPE = 'en_US.UTF-8';

CREATE USER admin_dev WITH ENCRYPTED PASSWORD 'admin_dev_password';

GRANT ALL PRIVILEGES ON DATABASE admin_dashboard_dev TO admin_dev;

\c admin_dashboard_dev postgres

GRANT ALL PRIVILEGES ON SCHEMA public TO admin_dev;

-- Create UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Exit psql
\q
```

### 3. Configure Application Properties

#### Create `application-dev.yml` in `src/main/resources/`
```yaml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:postgresql://localhost:5432/admin_dashboard_dev
    username: admin_dev
    password: admin_dev_password
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 10
          fetch_size: 50
  
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
    contexts: dev
  
  thymeleaf:
    cache: false
    encoding: UTF-8
    mode: HTML
    prefix: classpath:/templates/
    suffix: .html
    
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080
  servlet:
    context-path: /
    session:
      timeout: 30m

logging:
  level:
    root: INFO
    com.platform: DEBUG
    org.springframework: INFO
    org.hibernate: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

app:
  upload:
    dir: uploads
  jwt:
    secret: your-secret-key-change-in-production
    expiration: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,flyway,liquibase
  endpoint:
    health:
      show-details: always
```

### 4. Build the Project
```bash
# Clean and build
mvn clean install

# Build skipping tests (faster)
mvn clean install -DskipTests

# Build specific module
mvn -pl modules/user clean install
```

### 5. Run the Application

#### From IDE
1. Open project in IntelliJ IDEA or VS Code
2. Set Spring profile to `dev`
3. Run `AdminDashboardPlatformApplication` main class

#### From Command Line
```bash
# Development mode
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# With custom port
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev --server.port=9000"

# Using jar
mvn clean package -DskipTests
java -Dspring.profiles.active=dev -jar target/admin-dashboard-platform-1.0.0.jar
```

### 6. Access the Application
- **Web UI**: http://localhost:8080
- **API Docs**: http://localhost:8080/api (if Swagger configured)
- **Database Console**: http://localhost:8080/h2-console

### 7. Initial Data Setup

The application uses Liquibase for database migrations. On startup:
1. Liquibase runs all migrations from `db/changelog/`
2. Initial seed data is inserted automatically
3. You can log in with default credentials (check migration files)

## Project Structure

```
admin-dashboard-platform/
├── src/main/java/com/platform
│   ├── AdminDashboardPlatformApplication.java    # Main entry point
│   ├── core/
│   │   ├── base/                # Generic CRUD bases
│   │   │   ├── BaseEntity.java
│   │   │   ├── BaseRepository.java
│   │   │   ├── BaseService.java
│   │   │   ├── BaseServiceImpl.java
│   │   │   ├── BaseController.java
│   │   │   ├── ApiResponse.java
│   │   │   └── PaginationResponse.java
│   │   ├── config/              # Spring configurations
│   │   │   ├── SecurityConfig.java
│   │   │   ├── WebConfig.java
│   │   │   ├── CacheConfig.java
│   │   │   └── JacksonConfig.java
│   │   ├── exception/           # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── BusinessException.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── security/            # Security utilities
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── SecurityContextHolder.java
│   │   │   └── SecurityUtils.java
│   │   ├── audit/               # Audit logging
│   │   │   ├── AuditService.java
│   │   │   ├── AuditLogRepository.java
│   │   │   └── AuditLog.java
│   │   ├── translation/         # Internationalization
│   │   │   ├── TranslationService.java
│   │   │   ├── TranslationRepository.java
│   │   │   └── Translation.java
│   │   └── notification/        # Notifications
│   │       ├── NotificationService.java
│   │       └── WebSocketConfig.java
│   └── modules/
│       ├── auth/                # Authentication module
│       │   ├── controller/
│       │   ├── service/
│       │   ├── entity/
│       │   ├── dto/
│       │   └── repository/
│       ├── dashboard/           # Dashboard module
│       ├── user/                # User management
│       ├── role/                # Role management
│       ├── permission/          # Permissions
│       ├── menu/                # Dynamic menus
│       ├── project/             # Projects
│       ├── portfolio/           # Portfolios
│       ├── report/              # Reports
│       ├── analytics/           # Analytics
│       ├── settings/            # Settings
│       ├── file/                # File management
│       └── translation/         # Translation management
├── src/main/resources/
│   ├── application.yml          # Default config
│   ├── application-dev.yml      # Dev profile
│   ├── application-prod.yml     # Prod profile
│   ├── db/
│   │   └── changelog/           # Liquibase migrations
│   │       ├── db.changelog-master.yaml
│   │       ├── v001_*.yaml
│   │       └── v00N_*.yaml
│   └── templates/               # Thymeleaf templates
│       ├── layouts/
│       │   └── main-layout.html
│       ├── fragments/
│       │   ├── navbar.html
│       │   ├── sidebar.html
│       │   ├── footer.html
│       │   └── pagination.html
│       ├── pages/
│       │   ├── auth/
│       │   ├── dashboard/
│       │   ├── users/
│       │   ├── reports/
│       │   └── admin/
│       └── components/
│           ├── forms/
│           ├── tables/
│           └── modals/
├── src/test/java/
│   ├── com/platform/
│   │   ├── *ServiceTest.java    # Unit tests
│   │   └── *ControllerTest.java # Integration tests
│   └── resources/
│       └── test-data/
├── pom.xml                      # Maven configuration
├── README.md
├── API_DOCUMENTATION.md
└── PRODUCTION_DEPLOYMENT_GUIDE.md
```

## Development Workflow

### 1. Create a New Module

```bash
# Create module directories
mkdir -p src/main/java/com/platform/modules/{newmodule}/{controller,service/{impl},dto/{request,response},entity,mapper,repository,validator}

# Create module structure files
cat > src/main/java/com/platform/modules/newmodule/entity/NewModule.java << 'EOF'
package com.platform.modules.newmodule.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "new_modules")
@EqualsAndHashCode(callSuper = true)
public class NewModule extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
EOF
```

### 2. Database Migration

Create a new migration file: `src/main/resources/db/changelog/v00X_create_new_modules_table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v00X_create_new_modules_table
      author: developer
      changes:
        - createTable:
            tableName: new_modules
            columns:
              - column:
                  name: id
                  type: uuid
                  defaultValueComputed: uuid_generate_v4()
                  constraints:
                    primaryKey: true
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: description
                  type: TEXT
              - column:
                  name: created_by
                  type: uuid
              - column:
                  name: updated_by
                  type: uuid
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
              - column:
                  name: updated_at
                  type: TIMESTAMP
              - column:
                  name: deleted
                  type: BOOLEAN
                  defaultValue: false
        - createIndex:
            tableName: new_modules
            indexName: idx_new_modules_name
            columns:
              - column:
                  name: name
```

Add to `db.changelog-master.yaml`:
```yaml
  - include:
      file: v00X_create_new_modules_table.yaml
      relativeToChangelogFile: true
```

### 3. Create Service Layer

```bash
cat > src/main/java/com/platform/modules/newmodule/service/NewModuleService.java << 'EOF'
package com.platform.modules.newmodule.service;

import com.platform.core.base.BaseService;
import com.platform.modules.newmodule.dto.request.NewModuleRequest;
import com.platform.modules.newmodule.dto.response.NewModuleResponse;

public interface NewModuleService extends BaseService<NewModuleRequest, NewModuleResponse> {
    // Add custom methods if needed
}
EOF
```

### 4. Create Controller

```bash
cat > src/main/java/com/platform/modules/newmodule/controller/NewModuleController.java << 'EOF'
package com.platform.modules.newmodule.controller;

import com.platform.core.base.BaseController;
import com.platform.modules.newmodule.dto.request.NewModuleRequest;
import com.platform.modules.newmodule.dto.response.NewModuleResponse;
import com.platform.modules.newmodule.service.NewModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/new-modules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NewModuleController extends BaseController<NewModuleRequest, NewModuleResponse, NewModuleService> {
    
    public NewModuleController(NewModuleService service) {
        super(service);
    }
}
EOF
```

### 5. Create UI Controller

```bash
cat > src/main/java/com/platform/modules/newmodule/controller/NewModuleUIController.java << 'EOF'
package com.platform.modules.newmodule.controller;

import com.platform.modules.newmodule.service.NewModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/new-modules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NewModuleUIController {

    private final NewModuleService newModuleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "New Modules");
        return "pages/new-modules/list";
    }
}
EOF
```

## Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage report
mvn clean test jacoco:report
# Report location: target/site/jacoco/index.html
```

### Integration Tests
```bash
# Run integration tests only
mvn verify -P integration-tests

# Run specific integration test
mvn verify -Dtest=UserControllerIT
```

### Running Tests in IDE
1. Right-click on test file or test class
2. Select "Run" or "Run with Coverage"
3. View test results in the IDE panel

## Debugging

### Enable Debug Mode
```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--debug"
```

### Debug in IDE
1. Set breakpoints in code
2. Run application in Debug mode (Shift+F9 in IntelliJ)
3. Step through code with F10/F11

### Remote Debugging
```bash
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  -jar target/admin-dashboard-platform-1.0.0.jar
```

## Common Tasks

### Database Migrations

#### View Migration Status
```bash
mvn liquibase:status
```

#### Rollback Last Migration
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

#### Clear Database (Dev Only)
```bash
psql -U admin_dev admin_dashboard_dev -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
```

### Build Optimization

#### Skip Tests During Build
```bash
mvn clean install -DskipTests
```

#### Clean Build with No Snapshot Updates
```bash
mvn clean install -U
```

#### Build for Production
```bash
mvn clean package -P prod -DskipTests
```

## Troubleshooting

### Issue: PostgreSQL Connection Failed
```bash
# Check PostgreSQL is running
pg_isready -h localhost -p 5432

# Check credentials
psql -U admin_dev -h localhost -d admin_dashboard_dev -c "SELECT 1;"

# Check application logs
grep -i "connection" logs/application.log
```

### Issue: Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9000"
```

### Issue: Lombok Not Working
```bash
# Clean Maven cache
rm -rf ~/.m2/repository/org/projectlombok/

# Rebuild
mvn clean install

# In IDE: Reload Gradle/Maven project and invalidate caches
```

### Issue: Liquibase Migration Failed
```bash
# View detailed logs
mvn liquibase:migrate -X

# Mark migration as executed (use with caution)
mvn liquibase:markNextChangeSetRan
```

## IDE Configuration

### IntelliJ IDEA
1. Open Project Structure (Cmd+;)
2. Set Project SDK to JDK 21
3. Set Maven home path
4. Enable Annotation Processing: Settings > Build > Compiler > Annotation Processors
5. Install plugins: Thymeleaf, Lombok, Spring Assistant

### VS Code
1. Install Extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support for VS Code
2. Create `.vscode/launch.json` for debugging
3. Create `.vscode/settings.json` with Java settings

## Git Workflow

### Branch Naming
- Feature: `feature/description`
- Bug: `bugfix/description`
- Release: `release/version`

### Commit Message Format
```
[TYPE] Brief description

Detailed explanation if needed

Fixes #123
Related-To: #456
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Pull Request
1. Create feature branch
2. Commit changes
3. Push and create PR
4. Code review
5. Merge to main

## Performance Testing

### Load Testing with JMeter
```bash
# Run load test
jmeter -n -t performance-test.jmx -l results.jtl -j jmeter.log

# Generate report
jmeter -g results.jtl -o report/
```

### Application Metrics
```
GET http://localhost:8080/actuator/metrics
GET http://localhost:8080/actuator/prometheus
```

## Helpful Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [Thymeleaf Guide](https://www.thymeleaf.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## Support

For issues, questions, or suggestions:
- Create an issue: https://github.com/your-org/admin-dashboard/issues
- Start a discussion: https://github.com/your-org/admin-dashboard/discussions
- Contact team: dev-team@yourdomain.com
