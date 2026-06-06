package com.platform.modules.analytics.service;

import com.platform.modules.analytics.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    
    /**
     * Get analytics summary with current metrics and trends
     */
    AnalyticsSummaryResponse getAnalyticsSummary();
    
    /**
     * Get daily statistics for a date range
     */
    List<DailyStatisticResponse> getDailyStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get user growth metrics for a date range
     */
    List<UserGrowthResponse> getUserGrowth(int months);
    
    /**
     * Get action frequency distribution
     */
    List<ActionFrequencyResponse> getActionFrequency(int days);
    
    /**
     * Get monthly login trends
     */
    List<DailyStatisticResponse> getMonthlyLoginTrends(int months);
    
    /**
     * Get active users for a specific date
     */
    Long getActiveUsersForDate(LocalDate date);
    
    /**
     * Get new users for a specific date
     */
    Long getNewUsersForDate(LocalDate date);
}
