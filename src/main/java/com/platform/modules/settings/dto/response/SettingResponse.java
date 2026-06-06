package com.platform.modules.settings.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {
    @JsonProperty("key")
    private String settingKey;

    @JsonProperty("value")
    private Object settingValue;

    private String category;
    private String dataType;
    private String description;
}
