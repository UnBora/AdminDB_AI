package com.platform.modules.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.modules.project.dto.response.ProjectResponse;
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
public class PortfolioResponse {

    private UUID id;

    private String title;

    private String description;

    private String imageUrl;

    private ProjectResponse project;

    private UserResponse owner;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
