package com.platform.modules.user.service.impl;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.base.PaginationResponse;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.user.dto.request.UserCreateRequest;
import com.platform.modules.user.dto.request.UserUpdateRequest;
import com.platform.modules.user.dto.response.UserResponse;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.entity.Role;
import com.platform.modules.user.mapper.UserMapper;
import com.platform.modules.user.model.FilterCriteria;
import com.platform.modules.user.repository.RoleRepository;
import com.platform.modules.user.repository.UserRepository;
import com.platform.modules.user.service.UserService;
import com.platform.modules.user.specification.UserSpecification;
import com.platform.modules.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, UserResponse, UserRepository> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, 
                         PasswordEncoder passwordEncoder, UserValidator userValidator) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }


    @Override
    public UserResponse create(UserCreateRequest request) {
        UserValidator.ValidationResult validationResult = userValidator.validateUserCreate(request);
        if (!validationResult.isValid()) {
            throw new BusinessException("User validation failed: " + 
                validationResult.getErrors().stream()
                    .map(e -> e.getFieldName() + " - " + e.getMessage())
                    .collect(Collectors.joining(", ")));
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(request.getRoleIds()).stream().collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse create(UserResponse dto) {
        throw new UnsupportedOperationException("Use create(UserCreateRequest) instead");
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserValidator.ValidationResult validationResult = userValidator.validateUserUpdate(request, id);
        if (!validationResult.isValid()) {
            throw new BusinessException("User validation failed: " + 
                validationResult.getErrors().stream()
                    .map(e -> e.getFieldName() + " - " + e.getMessage())
                    .collect(Collectors.joining(", ")));
        }

        userMapper.updateEntity(request, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse update(UUID id, UserResponse dto) {
        throw new UnsupportedOperationException("Use update(UUID, UserUpdateRequest) instead");
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse assignRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.addRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse removeRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.removeRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse changeRoles(UUID userId, Set<UUID> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(roleIds).stream().collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(Set.of());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Search users by email with specification-based filtering
     */
    public PaginationResponse<UserResponse> searchByEmail(String email, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Specification<User> spec = UserSpecification.hasEmail(email);
        return searchWithSpecification(spec, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Search users by username with specification-based filtering
     */
    public PaginationResponse<UserResponse> searchByUsername(String username, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Specification<User> spec = UserSpecification.hasUsername(username);
        return searchWithSpecification(spec, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Search users with complex filters using dynamic FilterCriteria
     */
    public PaginationResponse<UserResponse> searchWithFilters(List<FilterCriteria> filters, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Specification<User> spec = UserSpecification.withFilters(filters);
        return searchWithSpecification(spec, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Search users by active status
     */
    public PaginationResponse<UserResponse> searchByActiveStatus(Boolean active, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Specification<User> spec = UserSpecification.isActive(active);
        return searchWithSpecification(spec, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Search users by created date range
     */
    public PaginationResponse<UserResponse> searchByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Specification<User> spec = UserSpecification.createdBetween(startDate, endDate);
        return searchWithSpecification(spec, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Helper method to execute specification-based search with pagination
     */
    private PaginationResponse<UserResponse> searchWithSpecification(Specification<User> spec, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        Page<User> page = userRepository.findAll(spec, pageable);

        return PaginationResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(userMapper::toResponse).toList())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Soft delete user by setting deleted flag
     */
    @Override
    public void softDelete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * Batch assign roles to a user
     */
    public UserResponse assignRoles(UUID userId, Set<UUID> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(roleIds).stream().collect(Collectors.toSet());
            for (Role role : roles) {
                user.addRole(role);
            }
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Batch remove roles from a user
     */
    public UserResponse removeRoles(UUID userId, Set<UUID> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(roleIds).stream().collect(Collectors.toSet());
            for (Role role : roles) {
                user.removeRole(role);
            }
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Export users to a list for external systems
     * This method can be used to prepare data for CSV/Excel export
     */
    public List<UserResponse> exportUsers(List<FilterCriteria> filters) {
        Specification<User> spec = UserSpecification.withFilters(filters);
        List<User> users = userRepository.findAll(spec);
        return users.stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Batch update user active status
     */
    public void batchUpdateActiveStatus(List<UUID> userIds, Boolean active) {
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.setActive(active));
        userRepository.saveAll(users);
    }

    /**
     * Get total count of active users
     */
    public long getActiveUserCount() {
        Specification<User> spec = UserSpecification.isActive(true);
        return userRepository.count(spec);
    }

    /**
     * Get total count of inactive users
     */
    public long getInactiveUserCount() {
        Specification<User> spec = UserSpecification.isActive(false);
        return userRepository.count(spec);
    }

    @Override
    protected UserResponse mapToDto(User entity) {
        return userMapper.toResponse(entity);
    }
}
