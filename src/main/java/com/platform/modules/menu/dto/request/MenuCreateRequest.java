package com.platform.modules.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCreateRequest {

    @NotBlank(message = "Menu name is required")
    @Size(min = 1, max = 100, message = "Menu name must be between 1 and 100 characters")
    private String name;

    @Size(max = 255, message = "Translation key must not exceed 255 characters")
    private String translationKey;

    @Size(max = 255, message = "URL must not exceed 255 characters")
    private String url;

    @Size(max = 100, message = "Icon must not exceed 100 characters")
    private String icon;

    private UUID parentId;

    private Integer orderNumber = 0;

    private Boolean active = true;
}
