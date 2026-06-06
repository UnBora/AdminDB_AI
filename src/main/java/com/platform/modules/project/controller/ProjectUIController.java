package com.platform.modules.project.controller;

import com.platform.modules.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class ProjectUIController {

    private final MenuService menuService;

    @GetMapping
    public String projectsPage(Model model) {
        model.addAttribute("menus", menuService.findAllRootMenus());
        return "pages/projects/project-list";
    }
}
