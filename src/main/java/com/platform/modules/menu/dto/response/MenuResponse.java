package com.platform.modules.menu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuResponse {

    private UUID id;
    private String name;
    private String translationKey;
    private String url;
    private String icon;
    private Integer orderNumber;
    private Boolean active;
    private List<MenuResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
