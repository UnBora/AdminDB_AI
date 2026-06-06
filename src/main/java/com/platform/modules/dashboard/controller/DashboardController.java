package com.platform.modules.dashboard.controller;

import com.platform.modules.menu.dto.response.MenuResponse;
import com.platform.modules.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MenuService menuService;

    @ModelAttribute
    public void addMenusToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Get user's primary role and fetch menus for that role
            // For now, using all root menus if no specific role is found
            List<MenuResponse> menus = menuService.findAllRootMenus();
            model.addAttribute("menus", menus);
            model.addAttribute("visibleMenus", menus);
        } else {
            model.addAttribute("menus", List.of());
            model.addAttribute("visibleMenus", List.of());
        }
    }

    @GetMapping
    public String dashboard() {
        return "pages/dashboard";
    }

    @GetMapping("/index")
    public String index() {
        return "pages/dashboard";
    }
}

