package com.platform.modules.report.service;

import com.platform.modules.report.dto.request.ReportRequest;
import com.platform.modules.report.dto.response.ReportResponse;
import com.platform.modules.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    Page<ReportResponse> searchReports(String name, Report.ReportType type, Report.ReportFormat format, Boolean isScheduled, Pageable pageable);
    Report create(ReportRequest request);
    Report update(UUID id, ReportRequest request);
    void delete(UUID id);
    Report getById(UUID id);
    ReportResponse mapToDto(Report entity);
    ReportResponse createReport(ReportRequest request, java.util.UUID createdBy);
    ByteArrayOutputStream exportToExcel(List<Report> reports);
    ByteArrayOutputStream exportToCSV(List<Report> reports);
    List<ReportResponse> getScheduledReports();
}
