# Enterprise Admin Dashboard Platform

A reusable, scalable, enterprise-level Admin Dashboard Platform built with Spring Boot, Spring Data JPA, PostgreSQL, Liquibase, and Thymeleaf.

## 🎯 Overview

This is a professional SaaS admin platform template designed to be:
- **Modular**: Each feature is self-contained and reusable
- **Scalable**: Ready for production deployment and future microservices decomposition
- **Database-Driven**: All configuration, translations, and settings in PostgreSQL
- **Secure**: Spring Security with role-based access control (RBAC)
- **Maintainable**: Clean architecture with SOLID principles

## 🏗️ Architecture

### Technology Stack

**Backend**
- Java 21
- Spring Boot 3.3.0
- Spring Security 6.x
- Spring Data JPA
- Hibernate ORM
- PostgreSQL 14+
- Liquibase

**Frontend**
- Thymeleaf 3.x
- Bootstrap 5
- Chart.js
- DataTables
- HTMX (for progressive enhancement)

**Build & Tools**
- Maven 3.8+
- Lombok
- MapStruct
- SLF4J + Logback

### Project Structure

```
src/main/java/com/platform/
├── core/                          # Core infrastructure
│   ├── base/                      # Generic CRUD patterns
│   │   ├── BaseEntity.java
│   │   ├── BaseRepository.java
│   │   ├── BaseService.java
│   │   ├── BaseServiceImpl.java
│   │   ├── BaseController.java
│   │   ├── ApiResponse.java
│   │   └── PaginationResponse.java
│   ├── config/                    # Spring configurations
│   │   ├── AppConfig.java
│   │   └── SecurityConfig.java
│   ├── constants/                 # App constants
│   ├── exception/                 # Global exception handling
│   ├── security/                  # Security components
│   ├── utils/                     # Utility classes
│   ├── audit/                     # Audit logging
│   ├── translation/               # Translation engine
│   └── notification/              # Notification system
│
├── modules/                       # Business modules
│   ├── auth/                      # Authentication
│   ├── user/                      # User management
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── mapper/
│   │   └── validator/
│   ├── role/                      # Role management
│   ├── permission/                # Permission management
│   ├── menu/                      # Dynamic menus
│   ├── dashboard/                 # Dashboard & widgets
│   ├── project/                   # Projects
│   ├── portfolio/                 # Portfolios
│   ├── report/                    # Reports & export
│   ├── analytics/                 # Analytics
│   ├── settings/                  # System settings
│   ├── file/                      # File upload/download
│   ├── translation/               # Translation management
│   ├── notification/              # Notifications
│   └── audit/                     # Audit logs
│
└── AdminDashboardPlatformApplication.java
```

## 🚀 Getting Started

### Prerequisites

- Java 21
- PostgreSQL 14+
- Maven 3.8+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/admin-dashboard-platform.git
cd admin-dashboard-platform
```

2. **Setup PostgreSQL Database**
```bash
# Create database
createdb admin_db

# Create user (optional)
createuser admin_user
```

3. **Update application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/admin_db
    username: postgres
    password: postgres
```

4. **Build the application**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

Or use your IDE to run: `AdminDashboardPlatformApplication.java`

6. **Access the application**
- URL: `http://localhost:8080`
- Default credentials:
  - Username: `admin`
  - Password: `admin123`

## 📦 Core Features Implemented

### Phase 1: ✅ Complete
- [x] Maven multi-module project setup
- [x] Spring Boot 3.x configuration
- [x] BaseEntity with UUID, audit fields, soft delete
- [x] Generic CRUD patterns (BaseRepository, BaseService, BaseController)
- [x] Exception handling with @ControllerAdvice
- [x] Spring Security configuration with BCryptPasswordEncoder
- [x] Liquibase database migrations (14 tables)
- [x] User, Role, Permission entities with relationships
- [x] Authentication service foundation
- [x] Translation system with caching
- [x] Basic Thymeleaf templates

### Phase 2-17: 🚧 In Progress
- [ ] Complete Auth module (login, logout, forgot password)
- [ ] Role & Permission management
- [ ] Dynamic sidebar menus
- [ ] User management CRUD
- [ ] Audit logging system
- [ ] Notifications with WebSocket
- [ ] File upload service
- [ ] Report generation (Excel/PDF/CSV)
- [ ] Analytics dashboard
- [ ] Settings management
- [ ] Project & Portfolio modules
- [ ] Reusable UI components
- [ ] Testing & documentation

## 🗄️ Database Schema

### Core Tables
- **users**: User accounts with roles
- **roles**: User roles
- **permissions**: Fine-grained permissions
- **user_roles**: User-Role many-to-many
- **role_permissions**: Role-Permission many-to-many

### Supporting Tables
- **menus**: Dynamic sidebar navigation
- **translations**: Multi-language support
- **audit_logs**: Change tracking with JSONB
- **notifications**: User notifications
- **app_settings**: System configuration with JSONB
- **file_metadata**: File upload tracking
- **dashboard_widgets**: User dashboard customization
- **projects**: Project management
- **portfolios**: Portfolio items

All tables support:
- UUID primary keys
- Soft deletes (`deleted` field)
- Audit timestamps (`created_at`, `updated_at`)
- Audit user tracking (`created_by`, `updated_by`)

## 🔐 Security Features

- Spring Security with method-level security
- @PreAuthorize for endpoint protection
- BCryptPasswordEncoder for password hashing
- CSRF protection
- XSS prevention
- Session management
- Role-based access control (RBAC)

## 🌐 Translation System

All UI text is database-driven:

```java
// In Thymeleaf templates:
<button th:text="${@translationService.get('button.submit')}">Submit</button>

// In Java code:
String label = translationService.get("label.user.name");
String localizedLabel = translationService.get("label.user.name", "km");
```

Supported locales: `en`, `km`, `ko`

## 📝 API Response Format

All APIs return consistent response format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { },
  "statusCode": 200,
  "timestamp": "2024-05-23T16:03:28Z"
}
```

## 📊 Pagination

List endpoints support:
- `pageNumber`: 0-based page index (default: 0)
- `pageSize`: Items per page (default: 10)
- `sortBy`: Field to sort by (default: createdAt)
- `sortDirection`: "asc" or "desc" (default: desc)

Example:
```
GET /api/users?pageNumber=0&pageSize=20&sortBy=username&sortDirection=asc
```

## 🔄 Module Structure

Each module follows this pattern:

```
module/
├── entity/           # JPA entities
├── repository/       # Spring Data repositories
├── service/          # Business logic interfaces
│   └── impl/         # Service implementations
├── controller/       # REST controllers
├── dto/
│   ├── request/     # Request DTOs
│   └── response/    # Response DTOs
├── mapper/          # MapStruct mappers
├── specification/   # JPA Specifications for filtering
└── validator/       # Custom validators
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

## 📦 Building for Production

```bash
# Clean build
mvn clean package -DskipTests

# Run with production profile
java -jar target/admin-dashboard-platform-1.0.0.jar --spring.profiles.active=prod
```

## 🔧 Configuration

### Profiles

- `dev`: Development with detailed logging and hot reload
- `prod`: Production with security hardening and performance optimization

### Environment Variables

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/admin_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=secure_password
SERVER_PORT=8080
LOGGING_LEVEL_ROOT=INFO
```

## 📚 API Documentation

See [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) for complete API reference.

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/my-feature`
2. Commit changes: `git commit -m 'Add my feature'`
3. Push to branch: `git push origin feature/my-feature`
4. Create a Pull Request

## 📄 License

MIT License - see LICENSE file for details

## 🙏 Acknowledgments

Built with:
- Spring Boot team
- Bootstrap community
- Thymeleaf community

## 📞 Support

For issues, questions, or suggestions, please open an issue on GitHub.

---

**Last Updated**: May 23, 2024
**Version**: 1.0.0
**Status**: Active Development 🚀
