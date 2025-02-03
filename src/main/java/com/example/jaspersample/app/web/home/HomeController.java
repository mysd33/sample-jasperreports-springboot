package com.example.jaspersample.app.web.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップページのController
 */
@Controller
public class HomeController {

    /**
     * トップページの表示
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
