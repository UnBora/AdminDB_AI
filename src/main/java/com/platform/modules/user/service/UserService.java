package com.platform.modules.user.service;

import com.platform.core.base.BaseService;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.user.dto.request.UserCreateRequest;
import com.platform.modules.user.dto.request.UserUpdateRequest;
import com.platform.modules.user.dto.response.UserResponse;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.model.FilterCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService extends BaseService<User, UserResponse> {

    UserResponse create(UserCreateRequest request);

    UserResponse update(UUID id, UserUpdateRequest request);

    UserResponse getByUsername(String username);

    UserResponse getByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserResponse assignRole(UUID userId, UUID roleId);

    UserResponse removeRole(UUID userId, UUID roleId);

    UserResponse changeRoles(UUID userId, Set<UUID> roleIds);

    PaginationResponse<UserResponse> searchByEmail(String email, int pageNumber, int pageSize, String sortBy, String sortDirection);

    PaginationResponse<UserResponse> searchByUsername(String username, int pageNumber, int pageSize, String sortBy, String sortDirection);

    PaginationResponse<UserResponse> searchWithFilters(List<FilterCriteria> filters, int pageNumber, int pageSize, String sortBy, String sortDirection);

    PaginationResponse<UserResponse> searchByActiveStatus(Boolean active, int pageNumber, int pageSize, String sortBy, String sortDirection);

    PaginationResponse<UserResponse> searchByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize, String sortBy, String sortDirection);

    UserResponse assignRoles(UUID userId, Set<UUID> roleIds);

    UserResponse removeRoles(UUID userId, Set<UUID> roleIds);

    List<UserResponse> exportUsers(List<FilterCriteria> filters);

    void batchUpdateActiveStatus(List<UUID> userIds, Boolean active);

    long getActiveUserCount();

    long getInactiveUserCount();
}
