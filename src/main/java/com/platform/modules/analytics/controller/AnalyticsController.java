package com.platform.modules.analytics.controller;

import com.platform.core.base.ApiResponse;
import com.platform.modules.analytics.dto.response.*;
import com.platform.modules.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/analytics/summary - Get analytics summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary() {
        AnalyticsSummaryResponse summary = analyticsService.getAnalyticsSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * GET /api/analytics/daily?start=2024-01-01&end=2024-01-31 - Get daily statistics
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyStatisticResponse>>> getDailyStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        LocalDate startDate = start != null ? start : LocalDate.now().minusDays(30);
        LocalDate endDate = end != null ? end : LocalDate.now();
        
        List<DailyStatisticResponse> stats = analyticsService.getDailyStatistics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * GET /api/analytics/user-growth?months=12 - Get user growth metrics
     */
    @GetMapping("/user-growth")
    public ResponseEntity<ApiResponse<List<UserGrowthResponse>>> getUserGrowth(
            @RequestParam(defaultValue = "12") int months) {
        
        List<UserGrowthResponse> growth = analyticsService.getUserGrowth(months);
        return ResponseEntity.ok(ApiResponse.success(growth));
    }

    /**
     * GET /api/analytics/action-frequency?days=30 - Get action frequency
     */
    @GetMapping("/action-frequency")
    public ResponseEntity<ApiResponse<List<ActionFrequencyResponse>>> getActionFrequency(
            @RequestParam(defaultValue = "30") int days) {
        
        List<ActionFrequencyResponse> frequency = analyticsService.getActionFrequency(days);
        return ResponseEntity.ok(ApiResponse.success(frequency));
    }

    /**
     * GET /api/analytics/login-trends?months=12 - Get login trends
     */
    @GetMapping("/login-trends")
    public ResponseEntity<ApiResponse<List<DailyStatisticResponse>>> getLoginTrends(
            @RequestParam(defaultValue = "12") int months) {
        
        List<DailyStatisticResponse> trends = analyticsService.getMonthlyLoginTrends(months);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    /**
     * GET /api/analytics/active-users?date=2024-01-01 - Get active users for a date
     */
    @GetMapping("/active-users")
    public ResponseEntity<ApiResponse<Long>> getActiveUsersForDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate queryDate = date != null ? date : LocalDate.now();
        Long activeUsers = analyticsService.getActiveUsersForDate(queryDate);
        return ResponseEntity.ok(ApiResponse.success(activeUsers));
    }

    /**
     * GET /api/analytics/new-users?date=2024-01-01 - Get new users for a date
     */
    @GetMapping("/new-users")
    public ResponseEntity<ApiResponse<Long>> getNewUsersForDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate queryDate = date != null ? date : LocalDate.now();
        Long newUsers = analyticsService.getNewUsersForDate(queryDate);
        return ResponseEntity.ok(ApiResponse.success(newUsers));
    }
}
