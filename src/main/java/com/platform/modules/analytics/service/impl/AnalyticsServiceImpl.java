package com.platform.modules.analytics.service.impl;

import com.platform.modules.analytics.dto.response.*;
import com.platform.modules.analytics.service.AnalyticsService;
import com.platform.modules.audit.entity.AuditAction;
import com.platform.modules.audit.repository.AuditRepository;
import com.platform.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        
        var auditLogs = auditRepository.findAll();
        long totalLogins = auditLogs.stream()
                .filter(log -> AuditAction.LOGIN.equals(log.getAction())).count();
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
            final LocalDate currentDate = date; // Make a final copy for lambda
            long activeUsers = getActiveUsersForDate(date);
            long newUsers = getNewUsersForDate(date);
            
            var logsForDay = auditRepository.findAll().stream()
                    .filter(log -> log.getCreatedAt() != null && 
                           log.getCreatedAt().toLocalDate().equals(currentDate))
                    .collect(Collectors.toList());
            
            long totalLogins = logsForDay.stream()
                    .filter(log -> AuditAction.LOGIN.equals(log.getAction())).count();
            long totalActions = logsForDay.size();

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
            long totalUsers = userRepository.count();
            long newUsers = getNewUsersInMonth(month);
            long activeUsers = getActiveUsersInMonth(month);
            
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
        var auditLogs = auditRepository.findAll();
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        
        var recentLogs = auditLogs.stream()
                .filter(log -> log.getCreatedAt() != null && 
                       log.getCreatedAt().toLocalDate().isAfter(cutoffDate))
                .collect(Collectors.toList());
        
        final long totalActions = recentLogs.size();
        
        Map<String, Long> frequencyMap = new HashMap<>();
        for (var log : recentLogs) {
            String action = log.getAction() != null ? log.getAction().toString() : "UNKNOWN";
            frequencyMap.put(action, frequencyMap.getOrDefault(action, 0L) + 1);
        }
        
        return frequencyMap.entrySet().stream()
                .map(entry -> ActionFrequencyResponse.builder()
                        .action(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalActions > 0 ? (entry.getValue() * 100.0 / totalActions) : 0.0)
                        .build())
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyStatisticResponse> getMonthlyLoginTrends(int months) {
        List<DailyStatisticResponse> trends = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            var logsForMonth = auditRepository.findAll().stream()
                    .filter(log -> log.getCreatedAt() != null && 
                           log.getCreatedAt().toLocalDate().getYear() == date.getYear() &&
                           log.getCreatedAt().toLocalDate().getMonthValue() == date.getMonthValue())
                    .collect(Collectors.toList());
            
            long totalLogins = logsForMonth.stream()
                    .filter(log -> AuditAction.LOGIN.equals(log.getAction())).count();
            
            trends.add(DailyStatisticResponse.builder()
                    .date(date)
                    .totalLogins(totalLogins)
                    .build());
        }
        
        return trends;
    }

    @Override
    public Long getActiveUsersForDate(LocalDate date) {
        return userRepository.count();
    }

    @Override
    public Long getNewUsersForDate(LocalDate date) {
        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && 
                       user.getCreatedAt().toLocalDate().equals(date))
                .count();
    }

    private long getNewUsersInMonth(YearMonth month) {
        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && 
                       YearMonth.from(user.getCreatedAt()).equals(month))
                .count();
    }

    private long getActiveUsersInMonth(YearMonth month) {
        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && 
                       YearMonth.from(user.getCreatedAt()).minusMonths(1).isBefore(month) &&
                       YearMonth.from(user.getCreatedAt()).isAfter(month.minusMonths(12)))
                .count();
    }
}
