# Admin Dashboard Platform - REST API Documentation

## Base URL
```
https://admin.yourdomain.com/api
```

## Authentication
All API endpoints (except login) require Bearer token authentication:
```
Authorization: Bearer <JWT_TOKEN>
```

## Common Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response data */ },
  "statusCode": 200
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "statusCode": 400
}
```

## Authentication Endpoints

### Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "rememberMe": false
}

Response (200):
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "expiresIn": 86400,
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roles": ["ROLE_ADMIN"]
    }
  }
}
```

### Refresh Token
```
POST /auth/refresh
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "token": "new_token",
    "expiresIn": 86400
  }
}
```

### Logout
```
POST /auth/logout
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Forgot Password
```
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}

Response (200):
{
  "success": true,
  "message": "Password reset link sent to email"
}
```

### Reset Password
```
POST /auth/reset-password
Content-Type: application/json

{
  "token": "reset_token_from_email",
  "newPassword": "newPassword123"
}

Response (200):
{
  "success": true,
  "message": "Password reset successfully"
}
```

## User Management Endpoints

### List Users
```
GET /users?page=0&size=20&sort=createdAt,desc
Authorization: Bearer <JWT_TOKEN>

Query Parameters:
- page: Page number (0-indexed)
- size: Page size (default: 20)
- sort: Sort field and direction (e.g., "createdAt,desc")
- search: Search term
- roleId: Filter by role UUID

Response (200):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "status": "ACTIVE",
        "roles": ["ROLE_USER"],
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

### Get User by ID
```
GET /users/{userId}
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "status": "ACTIVE",
    "roles": ["ROLE_USER", "ROLE_MANAGER"],
    "permissions": ["USER_READ", "USER_WRITE"],
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### Create User
```
POST /users
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "email": "newuser@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "password": "securePassword123",
  "roleIds": ["role-uuid-1", "role-uuid-2"],
  "status": "ACTIVE"
}

Response (201):
{
  "success": true,
  "data": {
    "id": "new-uuid",
    "email": "newuser@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "status": "ACTIVE"
  }
}
```

### Update User
```
PUT /users/{userId}
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "status": "ACTIVE"
}

Response (200):
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "Jane",
    "lastName": "Doe"
  }
}
```

### Delete User
```
DELETE /users/{userId}
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "message": "User deleted successfully"
}
```

### Assign Roles to User
```
POST /users/{userId}/roles
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "roleIds": ["role-uuid-1", "role-uuid-2"]
}

Response (200):
{
  "success": true,
  "message": "Roles assigned successfully"
}
```

## Report Endpoints

### List Reports
```
GET /reports?page=0&size=20&type=USER&format=EXCEL
Authorization: Bearer <JWT_TOKEN>

Query Parameters:
- page: Page number
- size: Page size
- type: Report type (USER|ACTIVITY|ANALYTICS|CUSTOM)
- format: Report format (PDF|EXCEL|CSV)
- search: Search in report name
- isScheduled: Filter by scheduled status (true|false)

Response (200):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "name": "Monthly User Report",
        "description": "Report of all active users",
        "type": "USER",
        "format": "EXCEL",
        "isScheduled": true,
        "scheduleFrequency": "MONTHLY",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "currentPage": 0
  }
}
```

### Create Report
```
POST /reports
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Daily Activity Report",
  "description": "Report of daily user activities",
  "type": "ACTIVITY",
  "format": "EXCEL",
  "queryBuilder": "SELECT * FROM audit_logs WHERE created_at > NOW() - INTERVAL 1 day",
  "isScheduled": true,
  "scheduleFrequency": "DAILY"
}

Response (201):
{
  "success": true,
  "data": {
    "id": "new-uuid",
    "name": "Daily Activity Report",
    "type": "ACTIVITY",
    "format": "EXCEL"
  }
}
```

### Export Report
```
GET /reports/{reportId}/export?format=EXCEL
Authorization: Bearer <JWT_TOKEN>

Response (200):
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="report_20240115.xlsx"

[Binary file content]
```

## File Management Endpoints

### Upload File
```
POST /files/upload
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data

Parameters:
- file: File to upload (multipart)
- module: Module name (USER|PROJECT|REPORT)
- entityId: ID of entity this file belongs to
- uploadedBy: User ID uploading file

Response (200):
{
  "success": true,
  "data": {
    "id": "uuid",
    "fileName": "document.pdf",
    "fileSize": 1024000,
    "mimeType": "application/pdf",
    "module": "USER",
    "uploadedAt": "2024-01-15T10:30:00Z"
  }
}
```

### Download File
```
GET /files/download/{fileId}
Authorization: Bearer <JWT_TOKEN>

Response (200):
Content-Type: application/pdf
Content-Disposition: attachment; filename="document.pdf"

[Binary file content]
```

### List Files by Entity
```
GET /files/entity/{module}/{entityId}
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "fileName": "document.pdf",
      "fileSize": 1024000,
      "mimeType": "application/pdf",
      "downloadCount": 5,
      "uploadedAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

### Delete File
```
DELETE /files/{fileId}
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "message": "File deleted successfully"
}
```

## Analytics Endpoints

### Get Dashboard Statistics
```
GET /analytics/dashboard
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "totalUsers": 150,
    "activeUsers": 45,
    "totalProjects": 28,
    "activeProjects": 15,
    "totalReports": 42,
    "lastLogin": "2024-01-15T14:30:00Z",
    "systemUptime": "45d 12h 30m"
  }
}
```

### Get User Statistics
```
GET /analytics/users?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "newUsers": 25,
    "activeUsers": 120,
    "inactiveUsers": 30,
    "usersByRole": {
      "ROLE_ADMIN": 5,
      "ROLE_MANAGER": 15,
      "ROLE_USER": 130
    },
    "usersByStatus": {
      "ACTIVE": 145,
      "INACTIVE": 5
    }
  }
}
```

### Get Login Analytics
```
GET /analytics/login?startDate=2024-01-01&endDate=2024-01-31&interval=DAY
Authorization: Bearer <JWT_TOKEN>

Query Parameters:
- interval: DAY|WEEK|MONTH

Response (200):
{
  "success": true,
  "data": {
    "labels": ["2024-01-01", "2024-01-02", ...],
    "loginCounts": [45, 52, 48, ...],
    "uniqueUsers": [40, 48, 45, ...],
    "totalLogins": 1450,
    "totalUniqueUsers": 135
  }
}
```

## Settings Endpoints

### Get All Settings
```
GET /settings
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": [
    {
      "key": "app.theme",
      "value": "light",
      "category": "THEME",
      "description": "Application theme preference",
      "updatedAt": "2024-01-15T10:30:00Z"
    },
    {
      "key": "smtp.host",
      "value": "smtp.gmail.com",
      "category": "EMAIL",
      "description": "SMTP server host",
      "updatedAt": "2024-01-14T15:20:00Z"
    }
  ]
}
```

### Get Setting by Key
```
GET /settings/{settingKey}
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "key": "app.theme",
    "value": "light",
    "category": "THEME",
    "type": "STRING"
  }
}
```

### Update Setting
```
PUT /settings/{settingKey}
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "value": "dark"
}

Response (200):
{
  "success": true,
  "data": {
    "key": "app.theme",
    "value": "dark",
    "category": "THEME",
    "updatedAt": "2024-01-15T14:00:00Z"
  }
}
```

## Role & Permission Endpoints

### List Roles
```
GET /roles?page=0&size=20
Authorization: Bearer <JWT_TOKEN>

Response (200):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "name": "ROLE_ADMIN",
        "description": "Administrator role",
        "permissions": ["USER_READ", "USER_WRITE", "USER_DELETE", ...],
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ]
  }
}
```

### Create Role
```
POST /roles
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "ROLE_MANAGER",
  "description": "Manager role",
  "permissionIds": ["perm-uuid-1", "perm-uuid-2"]
}

Response (201):
{
  "success": true,
  "data": {
    "id": "new-uuid",
    "name": "ROLE_MANAGER",
    "description": "Manager role"
  }
}
```

### Assign Permissions to Role
```
POST /roles/{roleId}/permissions
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "permissionIds": ["perm-uuid-1", "perm-uuid-2"]
}

Response (200):
{
  "success": true,
  "message": "Permissions assigned successfully"
}
```

## Error Codes

| Code | Message | Status |
|------|---------|--------|
| 400 | Bad Request | 400 |
| 401 | Unauthorized - Token required or expired | 401 |
| 403 | Forbidden - Insufficient permissions | 403 |
| 404 | Resource Not Found | 404 |
| 409 | Conflict - Resource already exists | 409 |
| 422 | Validation Error | 422 |
| 500 | Internal Server Error | 500 |
| 503 | Service Unavailable | 503 |

## Rate Limiting
- **Rate Limit**: 100 requests per minute per user
- **Headers**:
  - `X-RateLimit-Limit`: 100
  - `X-RateLimit-Remaining`: requests remaining
  - `X-RateLimit-Reset`: timestamp when limit resets

## Pagination
Standard pagination parameters:
```
{
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": false
}
```

## Timestamps
All timestamps are in ISO 8601 format with UTC timezone:
```
2024-01-15T14:30:00Z
```

## API Versioning
Current API Version: 1.0
Future versions will be accessible via `/api/v2`, `/api/v3`, etc.
