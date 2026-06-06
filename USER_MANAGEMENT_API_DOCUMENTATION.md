# User Management REST API - Implementation Summary

## Overview
Comprehensive User Management REST API with 8 endpoints for creating, reading, updating, and managing users with role-based access control. All endpoints are secured with `@PreAuthorize("hasRole('ADMIN')")` and follow REST best practices.

## Implementation Details

### Technology Stack
- **Framework**: Spring Boot 3.x with Spring Security
- **Database**: JPA/Hibernate with dynamic specifications
- **Password Encoding**: BCryptPasswordEncoder
- **Validation**: Jakarta Bean Validation
- **Mapping**: MapStruct
- **Response Format**: JSON with ApiResponse<T> wrapper

### Base Configuration
- **Base URL**: `/api/users`
- **Security**: All endpoints require ADMIN role
- **Authentication**: Spring Security
- **Request/Response**: JSON content-type

---

## Endpoint Specifications

### 1. List All Users with Pagination and Filtering
**Endpoint**: `GET /api/users`

**Description**: Retrieve paginated list of users with optional filtering and sorting.

**Query Parameters**:
- `page` (int, default=0): Page number (0-indexed)
- `size` (int, default=10): Number of records per page
- `sort` (string, default=createdAt): Field to sort by
- `direction` (string, default=desc): Sort direction (asc/desc)
- `firstName` (string, optional): Filter by first name (LIKE)
- `lastName` (string, optional): Filter by last name (LIKE)
- `email` (string, optional): Filter by email (LIKE)
- `active` (boolean, optional): Filter by active status

**Request Example**:
```
GET /api/users?page=0&size=10&sort=createdAt&direction=desc&active=true
```

**Response**: 
```json
{
  "success": true,
  "message": "Operation successful",
  "statusCode": 200,
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "username": "john.doe",
        "email": "john@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "avatarUrl": "https://example.com/avatar.jpg",
        "active": true,
        "lastLoginAt": "2026-05-25T10:30:00",
        "roles": [
          {
            "id": "550e8400-e29b-41d4-a716-446655440001",
            "name": "ADMIN",
            "description": "Administrator role",
            "permissions": [],
            "createdAt": "2026-05-20T08:00:00",
            "updatedAt": "2026-05-20T08:00:00"
          }
        ],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-25T10:30:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**HTTP Status**: 200 OK

**Features**:
- Dynamic filtering using UserSpecification
- Support for LIKE queries on text fields
- Pagination with configurable page size
- Multi-field sorting

---

### 2. Get Single User
**Endpoint**: `GET /api/users/{id}`

**Description**: Retrieve detailed information for a specific user including all assigned roles.

**Path Parameters**:
- `id` (UUID, required): User ID

**Request Example**:
```
GET /api/users/550e8400-e29b-41d4-a716-446655440000
```

**Response**:
```json
{
  "success": true,
  "message": "Operation successful",
  "statusCode": 200,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john.doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "active": true,
    "lastLoginAt": "2026-05-25T10:30:00",
    "roles": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "ADMIN",
        "description": "Administrator role",
        "permissions": [],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-20T08:00:00"
      }
    ],
    "createdAt": "2026-05-20T08:00:00",
    "updatedAt": "2026-05-25T10:30:00"
  }
}
```

**HTTP Status**: 
- 200 OK: User found
- 404 Not Found: User not found
- 403 Forbidden: Insufficient permissions

---

### 3. Create New User
**Endpoint**: `POST /api/users`

**Description**: Create a new user account with email uniqueness validation and password encryption.

**Request Body**:
```json
{
  "username": "jane.smith",
  "email": "jane@example.com",
  "password": "SecurePass123!",
  "firstName": "Jane",
  "lastName": "Smith",
  "avatarUrl": "https://example.com/avatar2.jpg",
  "active": true,
  "roleIds": [
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002"
  ]
}
```

**Validation Rules**:
- `username`: Required, 3-100 characters, must be unique
- `email`: Required, valid email format, must be unique
- `password`: Required, minimum 8 characters, encrypted with BCrypt
- `firstName`: Optional
- `lastName`: Optional
- `avatarUrl`: Optional
- `active`: Optional, defaults to true
- `roleIds`: Optional

**Response**:
```json
{
  "success": true,
  "message": "User created successfully",
  "statusCode": 201,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "username": "jane.smith",
    "email": "jane@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "avatarUrl": "https://example.com/avatar2.jpg",
    "active": true,
    "lastLoginAt": null,
    "roles": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "ADMIN",
        "description": "Administrator role",
        "permissions": [],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-20T08:00:00"
      }
    ],
    "createdAt": "2026-05-25T12:00:00",
    "updatedAt": "2026-05-25T12:00:00"
  }
}
```

**HTTP Status**: 
- 201 Created: User created successfully
- 400 Bad Request: Validation error (duplicate email/username, weak password)
- 403 Forbidden: Insufficient permissions

**Security Features**:
- Password encrypted using BCryptPasswordEncoder
- Email uniqueness validation
- Username uniqueness validation
- Role assignment with validation

---

### 4. Update User
**Endpoint**: `PUT /api/users/{id}`

**Description**: Update user information. Email uniqueness is validated if email is changed.

**Path Parameters**:
- `id` (UUID, required): User ID

**Request Body**:
```json
{
  "username": "jane.smith.updated",
  "email": "jane.updated@example.com",
  "firstName": "Janet",
  "lastName": "Smith",
  "avatarUrl": "https://example.com/avatar2-updated.jpg",
  "active": true,
  "roleIds": [
    "550e8400-e29b-41d4-a716-446655440001"
  ]
}
```

**Note**: All fields are optional. Only provided fields are updated.

**Response**:
```json
{
  "success": true,
  "message": "User updated successfully",
  "statusCode": 200,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "username": "jane.smith.updated",
    "email": "jane.updated@example.com",
    "firstName": "Janet",
    "lastName": "Smith",
    "avatarUrl": "https://example.com/avatar2-updated.jpg",
    "active": true,
    "lastLoginAt": null,
    "roles": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "ADMIN",
        "description": "Administrator role",
        "permissions": [],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-20T08:00:00"
      }
    ],
    "createdAt": "2026-05-25T12:00:00",
    "updatedAt": "2026-05-25T12:30:00"
  }
}
```

**HTTP Status**: 
- 200 OK: User updated successfully
- 400 Bad Request: Validation error (duplicate email)
- 404 Not Found: User not found
- 403 Forbidden: Insufficient permissions

**Validation**:
- Email uniqueness validation (only if email is being changed)
- Username validation if being changed

---

### 5. Delete User (Soft Delete)
**Endpoint**: `DELETE /api/users/{id}`

**Description**: Soft delete a user (marks deleted=true without removing from database).

**Path Parameters**:
- `id` (UUID, required): User ID

**Request Example**:
```
DELETE /api/users/550e8400-e29b-41d4-a716-446655440003
```

**Response**: No content

**HTTP Status**: 
- 204 No Content: User deleted successfully
- 404 Not Found: User not found
- 403 Forbidden: Insufficient permissions

**Features**:
- Soft delete preserves data integrity
- User record remains in database with deleted flag
- Can be restored by reverting deleted flag if needed

---

### 6. Assign Roles to User
**Endpoint**: `POST /api/users/{id}/roles`

**Description**: Assign roles to a user. Replaces all existing roles with the new set.

**Path Parameters**:
- `id` (UUID, required): User ID

**Request Body**:
```json
[
  "550e8400-e29b-41d4-a716-446655440001",
  "550e8400-e29b-41d4-a716-446655440002",
  "550e8400-e29b-41d4-a716-446655440004"
]
```

**Response**:
```json
{
  "success": true,
  "message": "Roles assigned successfully",
  "statusCode": 200,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "username": "jane.smith",
    "email": "jane@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "avatarUrl": "https://example.com/avatar2.jpg",
    "active": true,
    "lastLoginAt": null,
    "roles": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "ADMIN",
        "description": "Administrator role",
        "permissions": [],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-20T08:00:00"
      },
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "name": "USER",
        "description": "Regular user role",
        "permissions": [],
        "createdAt": "2026-05-20T08:00:00",
        "updatedAt": "2026-05-20T08:00:00"
      }
    ],
    "createdAt": "2026-05-25T12:00:00",
    "updatedAt": "2026-05-25T12:45:00"
  }
}
```

**HTTP Status**: 
- 200 OK: Roles assigned successfully
- 404 Not Found: User or role not found
- 403 Forbidden: Insufficient permissions

**Behavior**:
- Replaces all existing roles with new set
- Empty role list is permitted (removes all roles)
- Non-existent role IDs will throw ResourceNotFoundException

---

### 7. Remove Role from User
**Endpoint**: `DELETE /api/users/{id}/roles/{roleId}`

**Description**: Remove a specific role from a user.

**Path Parameters**:
- `id` (UUID, required): User ID
- `roleId` (UUID, required): Role ID to remove

**Request Example**:
```
DELETE /api/users/550e8400-e29b-41d4-a716-446655440003/roles/550e8400-e29b-41d4-a716-446655440002
```

**Response**: No content

**HTTP Status**: 
- 204 No Content: Role removed successfully
- 404 Not Found: User or role not found
- 403 Forbidden: Insufficient permissions

---

### 8. Export Users
**Endpoint**: `POST /api/users/export`

**Description**: Export filtered users data. Supports optional filtering and returns data ready for CSV/Excel export.

**Query Parameters**:
- `format` (string, default=csv): Export format (csv/excel) - informational for client
- `firstName` (string, optional): Filter by first name
- `lastName` (string, optional): Filter by last name
- `email` (string, optional): Filter by email
- `active` (boolean, optional): Filter by active status

**Request Example**:
```
POST /api/users/export?format=csv&active=true
```

**Response**:
```json
{
  "success": true,
  "message": "Users exported successfully",
  "statusCode": 200,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "john.doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "avatarUrl": "https://example.com/avatar.jpg",
      "active": true,
      "lastLoginAt": "2026-05-25T10:30:00",
      "roles": [
        {
          "id": "550e8400-e29b-41d4-a716-446655440001",
          "name": "ADMIN",
          "description": "Administrator role",
          "permissions": [],
          "createdAt": "2026-05-20T08:00:00",
          "updatedAt": "2026-05-20T08:00:00"
        }
      ],
      "createdAt": "2026-05-20T08:00:00",
      "updatedAt": "2026-05-25T10:30:00"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440003",
      "username": "jane.smith",
      "email": "jane@example.com",
      "firstName": "Jane",
      "lastName": "Smith",
      "avatarUrl": "https://example.com/avatar2.jpg",
      "active": true,
      "lastLoginAt": null,
      "roles": [],
      "createdAt": "2026-05-25T12:00:00",
      "updatedAt": "2026-05-25T12:00:00"
    }
  ]
}
```

**HTTP Status**: 
- 200 OK: Export data retrieved
- 403 Forbidden: Insufficient permissions

**Features**:
- Dynamic filtering using FilterCriteria
- Returns data ready for client-side CSV/Excel conversion
- Multiple filtering options
- All user details including roles included

---

## Error Responses

All endpoints follow consistent error response format:

```json
{
  "success": false,
  "message": "Error description",
  "statusCode": 400
}
```

**Common Error Scenarios**:

| HTTP Status | Scenario | Message |
|---|---|---|
| 400 | Duplicate email | "User validation failed: email - Email already exists" |
| 400 | Weak password | "User validation failed: password - Password must be at least 8 characters" |
| 400 | Invalid email format | "User validation failed: email - Email must be valid" |
| 401 | Unauthorized | "Unauthorized access" |
| 403 | Missing ADMIN role | "Access Denied" |
| 404 | User not found | "User not found with id: {id}" |
| 404 | Role not found | "Role not found" |

---

## Security

### Authentication & Authorization
- All endpoints require `@PreAuthorize("hasRole('ADMIN')")`
- User must be authenticated via Spring Security
- ADMIN role is mandatory for all operations

### Password Security
- Passwords are encrypted using BCryptPasswordEncoder
- Passwords are never returned in responses
- Password strength validated (minimum 8 characters)

### Data Protection
- Soft deletes preserve data integrity
- User records marked with deleted flag
- Proper exception handling with security context

---

## Implementation Patterns

### DTO Pattern
```
UserCreateRequest → UserMapper → User Entity → UserResponse
UserUpdateRequest → UserMapper → User Entity → UserResponse
```

### Service Layer
- Specification-based dynamic filtering using Spring Data JPA
- Transaction management with @Transactional
- Business logic validation

### Response Wrapping
- All responses wrapped with ApiResponse<T>
- Consistent message format
- Status code included

### Pagination
- Page-based pagination using Spring Data
- Configurable page size
- Total count and page info included

---

## Success Criteria Met

✅ All 8 endpoints implemented
✅ Endpoints use UserSpecification for dynamic filtering
✅ Proper HTTP status codes (200, 201, 204, 400, 401, 403, 404)
✅ Request/response DTOs defined (UserCreateRequest, UserUpdateRequest, UserResponse)
✅ Follows REST best practices
✅ Security with role-based access control
✅ Password encryption with BCrypt
✅ Email uniqueness validation
✅ Soft delete implementation
✅ Pagination and sorting support

---

## Testing Endpoints

### Using cURL

**Create User**:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**List Users**:
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10&sort=createdAt&direction=desc" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Get User**:
```bash
curl -X GET http://localhost:8080/api/users/{id} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Update User**:
```bash
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "firstName": "Updated",
    "active": true
  }'
```

**Delete User**:
```bash
curl -X DELETE http://localhost:8080/api/users/{id} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Assign Roles**:
```bash
curl -X POST http://localhost:8080/api/users/{id}/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '["role-id-1", "role-id-2"]'
```

**Remove Role**:
```bash
curl -X DELETE http://localhost:8080/api/users/{id}/roles/{roleId} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Export Users**:
```bash
curl -X POST "http://localhost:8080/api/users/export?format=csv&active=true" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Configuration Required

No additional configuration required beyond existing Spring Boot setup.

**Dependencies** (should already be in pom.xml):
- Spring Boot Web Starter
- Spring Data JPA
- Spring Security
- Hibernate
- MapStruct
- Lombok
- Jakarta Bean Validation
