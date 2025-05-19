package com.jobportal.controllers;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;

    @Autowired
    public DashboardController(UsersService usersService, JobPostActivityService jobPostActivityService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        System.out.println("✅ [DEBUG] DashboardController activated");
        String username = authentication.getName();
        model.addAttribute("username", username);

        Object profile = usersService.getCurrentUserProfile();

        List<JobPostActivity> jobPosts = jobPostActivityService.getAll();
        model.addAttribute("jobPosts", jobPosts);

        // Optional: If you still want to add profile types
        if (profile instanceof RecruiterProfile) {
            model.addAttribute("recruiterProfile", (RecruiterProfile) profile);
        } else if (profile instanceof JobSeekerProfile) {
            model.addAttribute("jobSeekerProfile", (JobSeekerProfile) profile);
        }

        return "dashboard";
    }
}
