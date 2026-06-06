package com.platform.modules.user.controller;

import com.platform.core.base.ApiResponse;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.user.dto.request.UserCreateRequest;
import com.platform.modules.user.dto.request.UserUpdateRequest;
import com.platform.modules.user.dto.response.UserResponse;
import com.platform.modules.user.model.FilterCriteria;
import com.platform.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users - List all users with pagination and filtering
     * Supports filtering by: firstName, lastName, email, active status
     * Supports sorting by all fields
     *
     * @param page    Page number (0-indexed)
     * @param size    Page size
     * @param sort    Sort field
     * @param direction Sort direction (asc/desc)
     * @param firstName Filter by first name (LIKE query)
     * @param lastName  Filter by last name (LIKE query)
     * @param email     Filter by email (LIKE query)
     * @param active    Filter by active status
     * @return Paginated list of users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active) {

        List<FilterCriteria> filters = buildUserFilters(username, firstName, lastName, email, active);
        PaginationResponse<UserResponse> result = userService.searchWithFilters(filters, page, size, sort, direction);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/users/{id} - Get single user with full details including roles
     *
     * @param id User ID
     * @return User details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * POST /api/users - Create new user
     * Validates email uniqueness and password strength
     * Encrypts password using BCryptPasswordEncoder
     *
     * @param request User creation request with email, password, firstName, lastName, roleIds
     * @return Created user with 201 status
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User created successfully"));
    }

    /**
     * PUT /api/users/{id} - Update user
     * Validates email uniqueness if email is being changed
     *
     * @param id      User ID
     * @param request User update request
     * @return Updated user details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    /**
     * DELETE /api/users/{id} - Soft delete user
     * Marks deleted=true without removing the record from database
     *
     * @param id User ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/users/{id}/roles - Assign roles to user
     * Replaces all existing roles with the new ones
     *
     * @param id      User ID
     * @param roleIds List of role IDs to assign
     * @return Updated user with new roles
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
            @PathVariable UUID id,
            @RequestBody Set<UUID> roleIds) {
        UserResponse user = userService.changeRoles(id, roleIds);
        return ResponseEntity.ok(ApiResponse.success(user, "Roles assigned successfully"));
    }

    /**
     * DELETE /api/users/{id}/roles/{roleId} - Remove specific role from user
     *
     * @param id     User ID
     * @param roleId Role ID to remove
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID id,
            @PathVariable UUID roleId) {
        userService.removeRole(id, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/users/export - Export users to file
     * Supports format: excel or csv
     * Note: Actual file generation would require additional libraries (Apache POI, OpenCSV)
     * This endpoint provides the data export functionality
     *
     * @param format Export format (excel/csv)
     * @return List of users for export
     */
    @PostMapping("/export")
    public ResponseEntity<ApiResponse<List<UserResponse>>> exportUsers(
            @RequestParam(required = false, defaultValue = "csv") String format,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active) {

        List<UserResponse> users = userService.exportUsers(buildUserFilters(username, firstName, lastName, email, active));

        // Return data for export (format conversion handled by client or separate service)
        return ResponseEntity.ok(ApiResponse.success(users, "Users exported successfully"));
    }

    private List<FilterCriteria> buildUserFilters(String username, String firstName, String lastName, String email, Boolean active) {
        List<FilterCriteria> filters = new ArrayList<>();
        if (hasText(username)) {
            filters.add(FilterCriteria.builder()
                    .filterKey("username")
                    .value(username)
                    .operation(FilterCriteria.FilterOperation.LIKE)
                    .build());
        }
        if (hasText(firstName)) {
            filters.add(FilterCriteria.builder()
                    .filterKey("firstName")
                    .value(firstName)
                    .operation(FilterCriteria.FilterOperation.LIKE)
                    .build());
        }
        if (hasText(lastName)) {
            filters.add(FilterCriteria.builder()
                    .filterKey("lastName")
                    .value(lastName)
                    .operation(FilterCriteria.FilterOperation.LIKE)
                    .build());
        }
        if (hasText(email)) {
            filters.add(FilterCriteria.builder()
                    .filterKey("email")
                    .value(email)
                    .operation(FilterCriteria.FilterOperation.LIKE)
                    .build());
        }
        if (active != null) {
            filters.add(FilterCriteria.builder()
                    .filterKey("active")
                    .value(active)
                    .operation(FilterCriteria.FilterOperation.EQUAL)
                    .build());
        }

        return filters;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
