package com.platform.modules.report.service.impl;

import com.platform.modules.report.dto.request.ReportRequest;
import com.platform.modules.report.dto.response.ReportResponse;
import com.platform.modules.report.entity.Report;
import com.platform.modules.report.mapper.ReportMapper;
import com.platform.modules.report.repository.ReportRepository;
import com.platform.modules.report.service.ReportService;
import com.platform.modules.report.specification.ReportSpecification;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportRepository repository, ReportMapper reportMapper) {
        this.repository = repository;
        this.reportMapper = reportMapper;
    }

    @Override
    public Report create(ReportRequest dto) {
        Report report = reportMapper.toEntity(dto);
        return repository.save(report);
    }

    @Override
    public Report update(UUID id, ReportRequest dto) {
        Report report = repository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        report.setName(dto.getName());
        report.setDescription(dto.getDescription());
        report.setType(Report.ReportType.valueOf(dto.getType()));
        report.setFormat(Report.ReportFormat.valueOf(dto.getFormat()));
        report.setQueryBuilder(dto.getQueryBuilder());
        report.setIsScheduled(dto.getIsScheduled());
        report.setScheduleFrequency(dto.getScheduleFrequency());
        return repository.save(report);
    }

    @Override
    public Report getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public ReportResponse mapToDto(Report entity) {
        return reportMapper.toResponse(entity);
    }

    @Override
    public Page<ReportResponse> searchReports(String name, Report.ReportType type, Report.ReportFormat format, Boolean isScheduled, Pageable pageable) {
        Specification<Report> spec = Specification
            .where(ReportSpecification.searchByName(name))
            .and(ReportSpecification.withType(type))
            .and(ReportSpecification.withFormat(format))
            .and(ReportSpecification.isScheduled(isScheduled));
        
        return repository.findAll(spec, pageable).map(reportMapper::toResponse);
    }

    @Override
    public ReportResponse createReport(ReportRequest request, UUID createdBy) {
        Report report = create(request);
        report.setCreatedBy(createdBy);
        return mapToDto(repository.save(report));
    }

    @Override
    public ByteArrayOutputStream exportToExcel(List<Report> reports) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reports");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Description");
            headerRow.createCell(2).setCellValue("Type");
            headerRow.createCell(3).setCellValue("Format");
            headerRow.createCell(4).setCellValue("Created At");
            
            int rowNum = 1;
            for (Report report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getName());
                row.createCell(1).setCellValue(report.getDescription());
                row.createCell(2).setCellValue(report.getType().toString());
                row.createCell(3).setCellValue(report.getFormat().toString());
                row.createCell(4).setCellValue(report.getCreatedAt().toString());
            }
            
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Export to Excel failed", e);
        }
        
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportToCSV(List<Report> reports) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Name,Description,Type,Format,Created At\n");
            
            for (Report report : reports) {
                csv.append(escapeCsv(report.getName())).append(",");
                csv.append(escapeCsv(report.getDescription())).append(",");
                csv.append(report.getType()).append(",");
                csv.append(report.getFormat()).append(",");
                csv.append(report.getCreatedAt()).append("\n");
            }
            
            outputStream.write(csv.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Export to CSV failed", e);
        }
        
        return outputStream;
    }

    @Override
    public List<ReportResponse> getScheduledReports() {
        return repository.findByIsScheduledTrue()
            .stream()
            .map(reportMapper::toResponse)
            .collect(Collectors.toList());
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
