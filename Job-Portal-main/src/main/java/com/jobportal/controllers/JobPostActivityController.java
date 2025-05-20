package com.jobportal.controllers;

import com.jobportal.entity.*;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@Controller
public class JobPostActivityController {
    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
    }

    @GetMapping("/dashboard/")
    public String dashboard(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            Model model) {

        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(
                        ((RecruiterProfile) currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Job Seeker"))) {
                List<JobPostActivity> jobs;
                if (title != null || location != null) {
                    jobs = jobPostActivityService.searchJobs(title, location);
                } else {
                    jobs = jobPostActivityService.getAll();
                }
                model.addAttribute("allJobs", jobs);
            }
        }
        model.addAttribute("user", currentUserProfile);
        return "dashboard";
    }

    // ... keep all your existing methods unchanged ...
    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {
        Users user = usersService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }

    @GetMapping("/dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @GetMapping("/global-search/")
    public String globalSearch(
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            Model model) {

        List<JobPostActivity> searchResults = jobPostActivityService.searchJobs(job, location);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("job", job);
        model.addAttribute("location", location);

        // Return a new template for search results
        return "search-results";
    }
}