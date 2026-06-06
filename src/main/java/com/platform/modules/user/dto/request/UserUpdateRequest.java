package com.platform.modules.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @Email(message = "Email must be valid")
    private String email;

    private String firstName;

    private String lastName;

    private String avatarUrl;

    private Boolean active;

    private Set<UUID> roleIds;
}
