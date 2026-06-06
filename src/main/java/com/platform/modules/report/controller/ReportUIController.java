package com.platform.modules.report.controller;

import com.platform.modules.report.entity.Report;
import com.platform.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportUIController {

    private final ReportService reportService;

    @GetMapping
    public String showReportsList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            Model model) {
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("reportTypes", Report.ReportType.values());
        model.addAttribute("reportFormats", Report.ReportFormat.values());
        return "pages/reports/report-list";
    }

    @GetMapping("/generate")
    public String showReportGenerator(Model model) {
        model.addAttribute("reportTypes", Report.ReportType.values());
        model.addAttribute("reportFormats", Report.ReportFormat.values());
        return "pages/reports/report-generator";
    }

    @GetMapping("/{id}")
    public String showReportDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("report", reportService.mapToDto(reportService.getById(id)));
        model.addAttribute("reportFormats", Report.ReportFormat.values());
        return "pages/reports/report-detail";
    }
}
