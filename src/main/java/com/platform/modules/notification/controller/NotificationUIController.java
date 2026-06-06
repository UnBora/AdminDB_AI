package com.platform.modules.notification.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationUIController {

    /**
     * GET /notifications - Show notifications list page
     */
    @GetMapping
    public String showNotificationsList() {
        return "pages/notifications/notifications-list";
    }
}
