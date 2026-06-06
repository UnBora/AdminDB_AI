package com.platform.modules.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.modules.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponse {

    private UUID id;

    private String name;

    private String description;

    private String status;

    private UserResponse owner;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
