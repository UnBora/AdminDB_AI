package com.platform.core.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}
