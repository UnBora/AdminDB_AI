package com.platform.modules.report.specification;

import com.platform.modules.report.entity.Report;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    public static Specification<Report> withType(Report.ReportType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Report> withFormat(Report.ReportFormat format) {
        return (root, query, cb) -> format == null ? cb.conjunction() : cb.equal(root.get("format"), format);
    }

    public static Specification<Report> searchByName(String name) {
        return (root, query, cb) -> name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Report> isScheduled(Boolean scheduled) {
        return (root, query, cb) -> scheduled == null ? cb.conjunction() : cb.equal(root.get("isScheduled"), scheduled);
    }
}
