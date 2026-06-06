package com.platform.modules.settings.controller;

import com.platform.modules.settings.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SettingsUIController {

    private final SettingService settingService;

    @GetMapping
    public String showSettingsPage(Model model) {
        model.addAttribute("categories", SettingCategory.values());
        model.addAttribute("currentCategory", "THEME");
        return "pages/settings/settings-admin";
    }

    @GetMapping("/theme")
    public String showThemeSettings(Model model) {
        model.addAttribute("currentCategory", "THEME");
        return "pages/settings/settings-admin";
    }

    @GetMapping("/email")
    public String showEmailSettings(Model model) {
        model.addAttribute("currentCategory", "EMAIL");
        return "pages/settings/settings-admin";
    }

    @GetMapping("/security")
    public String showSecuritySettings(Model model) {
        model.addAttribute("currentCategory", "SECURITY");
        return "pages/settings/settings-admin";
    }

    enum SettingCategory {
        THEME, EMAIL, SECURITY, AUDIT, APPLICATION, USERS
    }
}
