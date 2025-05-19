package com.jobportal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String testPage() {
        System.out.println("🔥 /test page hit");
        return "job-details";
    }
}
