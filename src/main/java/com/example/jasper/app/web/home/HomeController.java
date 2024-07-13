package com.example.jasper.app.web.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップページのController
 */
@Controller
public class HomeController {
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
}
