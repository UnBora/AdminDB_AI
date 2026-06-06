package com.platform.modules.audit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditUIController {

    /**
     * GET /audit - Show audit logs list page
     */
    @GetMapping
    public String showAuditList() {
        return "pages/audit/audit-list";
    }
}
