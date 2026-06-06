package com.platform.modules.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String avatarUrl;

    private Boolean active;

    private LocalDateTime lastLoginAt;

    private Set<RoleResponse> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
