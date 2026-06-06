package com.platform.modules.analytics.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsUIController {

    /**
     * GET /analytics - Show analytics dashboard
     */
    @GetMapping
    public String showAnalyticsDashboard() {
        return "pages/analytics/analytics-dashboard";
    }
}
