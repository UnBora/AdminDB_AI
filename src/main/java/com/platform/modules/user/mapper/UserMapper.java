package com.platform.modules.user.mapper;

import com.platform.modules.user.dto.request.UserCreateRequest;
import com.platform.modules.user.dto.request.UserUpdateRequest;
import com.platform.modules.user.dto.response.PermissionResponse;
import com.platform.modules.user.dto.response.RoleResponse;
import com.platform.modules.user.dto.response.UserResponse;
import com.platform.modules.user.entity.Permission;
import com.platform.modules.user.entity.Role;
import com.platform.modules.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    UserResponse toResponse(User entity);

    RoleResponse toResponse(Role entity);

    PermissionResponse toResponse(Permission entity);
}
