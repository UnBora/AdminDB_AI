package com.platform.modules.report.controller;

import com.platform.core.base.ApiResponse;
import com.platform.modules.report.dto.request.ReportRequest;
import com.platform.modules.report.dto.response.ReportResponse;
import com.platform.modules.report.entity.Report;
import com.platform.modules.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ApiResponse<Page<ReportResponse>> getAllReports(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Boolean isScheduled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Report.ReportType reportType = type != null ? Report.ReportType.valueOf(type) : null;
        Report.ReportFormat reportFormat = format != null ? Report.ReportFormat.valueOf(format) : null;
        
        Page<ReportResponse> result = reportService.searchReports(name, reportType, reportFormat, isScheduled, pageable);
        return ApiResponse.success(result, "Reports retrieved successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getReport(@PathVariable UUID id) {
        return ApiResponse.success(reportService.mapToDto(reportService.getById(id)), "Report retrieved successfully");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponse> createReport(@Valid @RequestBody ReportRequest request, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.success(reportService.createReport(request, userId), "Report created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponse> updateReport(@PathVariable UUID id, @Valid @RequestBody ReportRequest request) {
        reportService.update(id, request);
        return ApiResponse.success(reportService.mapToDto(reportService.getById(id)), "Report updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteReport(@PathVariable UUID id) {
        reportService.delete(id);
        return ApiResponse.success(null, "Report deleted successfully");
    }

    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportToExcel(@PathVariable UUID id) {
        Report report = reportService.getById(id);
        ByteArrayOutputStream outputStream = reportService.exportToExcel(List.of(report));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", report.getName() + ".xlsx");
        
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/export/csv")
    public ResponseEntity<byte[]> exportToCSV(@PathVariable UUID id) {
        Report report = reportService.getById(id);
        ByteArrayOutputStream outputStream = reportService.exportToCSV(List.of(report));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", report.getName() + ".csv");
        
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/scheduled/list")
    public ApiResponse<List<ReportResponse>> getScheduledReports() {
        return ApiResponse.success(reportService.getScheduledReports(), "Scheduled reports retrieved successfully");
    }
}
