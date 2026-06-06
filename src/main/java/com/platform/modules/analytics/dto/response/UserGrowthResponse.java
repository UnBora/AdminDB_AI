package com.platform.modules.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGrowthResponse {
    private YearMonth month;
    private Long newUsers;
    private Long totalUsers;
    private Long activeUsers;
}
