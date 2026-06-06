package com.platform.modules.menu.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuTreeResponse {

    private List<MenuResponse> menus;
    private Long totalCount;
    private String cacheStatus;
}
