package com.platform.modules.report.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.report.entity.Report;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends BaseRepository<Report> {
    List<Report> findByType(Report.ReportType type);
    List<Report> findByFormat(Report.ReportFormat format);
    List<Report> findByIsScheduledTrue();
}
