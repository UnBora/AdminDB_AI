package com.platform.modules.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatisticResponse {
    private LocalDate date;
    private Long activeUsers;
    private Long newUsers;
    private Long totalLogins;
    private Long totalActions;
    private Long successfulLogins;
    private Long failedLogins;
    private BigDecimal averageSessionDuration;
}
