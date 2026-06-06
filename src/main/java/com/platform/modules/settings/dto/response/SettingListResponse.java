package com.platform.modules.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingListResponse {
    private List<SettingResponse> settings;
    private long totalCount;
    private int pageNumber;
    private int pageSize;
}
