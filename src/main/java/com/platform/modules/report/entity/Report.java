package com.platform.modules.report.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE reports SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Report extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportFormat format;

    @Column(name = "query_builder", columnDefinition = "TEXT")
    private String queryBuilder;

    @Column(name = "is_scheduled")
    private Boolean isScheduled = false;

    @Column(name = "schedule_frequency", length = 50)
    private String scheduleFrequency;

    @Column(name = "last_run_at")
    private java.time.LocalDateTime lastRunAt;

    public enum ReportType {
        USER, ACTIVITY, ANALYTICS, CUSTOM
    }

    public enum ReportFormat {
        PDF, EXCEL, CSV
    }
}
