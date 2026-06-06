package com.platform.modules.analytics.service.impl;

import com.platform.modules.analytics.dto.response.*;
import com.platform.modules.analytics.service.AnalyticsService;
import com.platform.modules.audit.entity.AuditAction;
import com.platform.modules.audit.repository.AuditRepository;
import com.platform.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;

    @Override
    public AnalyticsSummaryResponse getAnalyticsSummary() {
        long totalUsers = userRepository.count();
        long activeUsersToday = getActiveUsersForDate(LocalDate.now());
        long newUsersThisMonth = getNewUsersInMonth(YearMonth.now());
        
        long totalLogins = auditRepository.countByAction(AuditAction.LOGIN);
        long successfulLogins = totalLogins;
        long failedLogins = 0;

        double successRate = totalLogins > 0 ? (successfulLogins * 100.0 / totalLogins) : 0.0;
        double avgSessionDuration = 30.0;

        var dailyStats = getDailyStatistics(LocalDate.now().minusDays(30), LocalDate.now());
        var userGrowth = getUserGrowth(12);
        var actionFrequency = getActionFrequency(30);

        return AnalyticsSummaryResponse.builder()
                .totalUsers(totalUsers)
                .activeUsersToday(activeUsersToday)
                .newUsersThisMonth(newUsersThisMonth)
                .totalLogins(totalLogins)
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .successRate(java.math.BigDecimal.valueOf(successRate).setScale(2, java.math.RoundingMode.HALF_UP))
                .averageSessionDuration(java.math.BigDecimal.valueOf(avgSessionDuration).setScale(2, java.math.RoundingMode.HALF_UP))
                .dailyStats(dailyStats)
                .userGrowth(userGrowth)
                .actionFrequency(actionFrequency)
                .build();
    }

    @Override
    public List<DailyStatisticResponse> getDailyStatistics(LocalDate startDate, LocalDate endDate) {
        List<DailyStatisticResponse> stats = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            long activeUsers = getActiveUsersForDate(date);
            long newUsers = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            long totalLogins = auditRepository.countByActionAndCreatedAtBetween(AuditAction.LOGIN, startOfDay, endOfDay);
            long totalActions = auditRepository.countByCreatedAtBetween(startOfDay, endOfDay);

            stats.add(DailyStatisticResponse.builder()
                    .date(date)
                    .activeUsers(activeUsers)
                    .newUsers(newUsers)
                    .totalLogins(totalLogins)
                    .totalActions(totalActions)
                    .successfulLogins(totalLogins)
                    .failedLogins(0L)
                    .averageSessionDuration(java.math.BigDecimal.valueOf(30.0))
                    .build());
        }
        
        return stats;
    }

    @Override
    public List<UserGrowthResponse> getUserGrowth(int months) {
        List<UserGrowthResponse> growth = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);

            long totalUsers = userRepository.countByCreatedAtBetween(LocalDateTime.MIN, endOfMonth);
            long newUsers = userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
            long activeUsers = auditRepository.countByActionAndCreatedAtBetween(AuditAction.LOGIN, startOfMonth, endOfMonth);
            
            growth.add(UserGrowthResponse.builder()
                    .month(month)
                    .totalUsers(totalUsers)
                    .newUsers(newUsers)
                    .activeUsers(activeUsers)
                    .build());
        }
        
        return growth;
    }

    @Override
    public List<ActionFrequencyResponse> getActionFrequency(int days) {
        LocalDateTime cutoffDate = LocalDate.now().minusDays(days).atStartOfDay();
        List<Object[]> results = auditRepository.countActionsByTypeAfter(cutoffDate);
        
        long totalActions = results.stream().mapToLong(r -> (Long) r[1]).sum();
        
        return results.stream()
                .map(r -> ActionFrequencyResponse.builder()
                        .action(r[0].toString())
                        .count((Long) r[1])
                        .percentage(totalActions > 0 ? ((Long) r[1] * 100.0 / totalActions) : 0.0)
                        .build())
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyStatisticResponse> getMonthlyLoginTrends(int months) {
        List<DailyStatisticResponse> trends = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);

            long totalLogins = auditRepository.countByActionAndCreatedAtBetween(AuditAction.LOGIN, startOfMonth, endOfMonth);
            
            trends.add(DailyStatisticResponse.builder()
                    .date(month.atDay(1))
                    .totalLogins(totalLogins)
                    .build());
        }
        
        return trends;
    }

    @Override
    public Long getActiveUsersForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return auditRepository.countByActionAndCreatedAtBetween(AuditAction.LOGIN, startOfDay, endOfDay);
    }

    @Override
    public Long getNewUsersForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
    }

    private long getNewUsersInMonth(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);
        return userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
    }
}
