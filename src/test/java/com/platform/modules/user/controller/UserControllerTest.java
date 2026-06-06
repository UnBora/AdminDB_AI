package com.platform.modules.user.controller;

import com.platform.core.base.PaginationResponse;
import com.platform.modules.user.dto.response.UserResponse;
import com.platform.modules.user.model.FilterCriteria;
import com.platform.modules.user.service.UserService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {

    @Test
    void getAllUsersPassesQueryParametersAsFilters() {
        CapturingUserService userService = new CapturingUserService();
        UserController controller = new UserController(userService);

        controller.getAllUsers(1, 25, "email", "asc", "admin", "Ada", "Lovelace", "ada@example.com", true);

        assertThat(userService.pageNumber).isEqualTo(1);
        assertThat(userService.pageSize).isEqualTo(25);
        assertThat(userService.sortBy).isEqualTo("email");
        assertThat(userService.sortDirection).isEqualTo("asc");
        List<FilterCriteria> filters = userService.filters;
        assertThat(filters).hasSize(5);
        assertThat(filters)
                .extracting(FilterCriteria::getFilterKey)
                .containsExactly("username", "firstName", "lastName", "email", "active");
        assertThat(filters)
                .extracting(FilterCriteria::getValue)
                .containsExactly("admin", "Ada", "Lovelace", "ada@example.com", true);
    }

    @Test
    void exportUsersUsesTheSameFiltersAsListEndpoint() {
        CapturingUserService userService = new CapturingUserService();
        UserController controller = new UserController(userService);

        controller.exportUsers("csv", "admin", "Ada", "Lovelace", "ada@example.com", true);

        assertThat(userService.exportFilters)
                .extracting(FilterCriteria::getFilterKey)
                .containsExactly("username", "firstName", "lastName", "email", "active");
    }

    private static class CapturingUserService implements UserService {
        private List<FilterCriteria> filters;
        private List<FilterCriteria> exportFilters;
        private int pageNumber;
        private int pageSize;
        private String sortBy;
        private String sortDirection;

        @Override
        public PaginationResponse<UserResponse> searchWithFilters(List<FilterCriteria> filters, int pageNumber, int pageSize, String sortBy, String sortDirection) {
            this.filters = filters;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.sortBy = sortBy;
            this.sortDirection = sortDirection;
            return PaginationResponse.<UserResponse>builder().content(List.of()).build();
        }

        @Override
        public List<UserResponse> exportUsers(List<FilterCriteria> filters) {
            this.exportFilters = filters;
            return List.of();
        }

        @Override
        public UserResponse create(UserResponse dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse update(UUID id, UserResponse dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void softDelete(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse getById(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PaginationResponse<UserResponse> getAllPaginated(int pageNumber, int pageSize, String sortBy, String sortDirection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse create(com.platform.modules.user.dto.request.UserCreateRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse update(UUID id, com.platform.modules.user.dto.request.UserUpdateRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse getByUsername(String username) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse getByEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsByUsername(String username) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsByEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse assignRole(UUID userId, UUID roleId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse removeRole(UUID userId, UUID roleId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse changeRoles(UUID userId, Set<UUID> roleIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PaginationResponse<UserResponse> searchByEmail(String email, int pageNumber, int pageSize, String sortBy, String sortDirection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PaginationResponse<UserResponse> searchByUsername(String username, int pageNumber, int pageSize, String sortBy, String sortDirection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PaginationResponse<UserResponse> searchByActiveStatus(Boolean active, int pageNumber, int pageSize, String sortBy, String sortDirection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PaginationResponse<UserResponse> searchByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize, String sortBy, String sortDirection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse assignRoles(UUID userId, Set<UUID> roleIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserResponse removeRoles(UUID userId, Set<UUID> roleIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void batchUpdateActiveStatus(List<UUID> userIds, Boolean active) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getActiveUserCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getInactiveUserCount() {
            throw new UnsupportedOperationException();
        }
    }
}
