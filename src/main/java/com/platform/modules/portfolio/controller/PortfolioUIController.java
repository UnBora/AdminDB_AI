package com.platform.modules.portfolio.controller;

import com.platform.modules.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolios")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class PortfolioUIController {

    private final MenuService menuService;

    @GetMapping
    public String portfoliosPage(Model model) {
        model.addAttribute("menus", menuService.findAllRootMenus());
        return "pages/portfolios/portfolio-list";
    }
}
