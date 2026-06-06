package com.platform.modules.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingUpdateRequest {
    private Object value;
    private String category;
    private String description;
}
