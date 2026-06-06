package com.platform.modules.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {
    private Long totalUsers;
    private Long activeUsersToday;
    private Long newUsersThisMonth;
    private Long totalLogins;
    private Long successfulLogins;
    private Long failedLogins;
    private BigDecimal successRate;
    private BigDecimal averageSessionDuration;
    private List<DailyStatisticResponse> dailyStats;
    private List<UserGrowthResponse> userGrowth;
    private List<ActionFrequencyResponse> actionFrequency;
}
