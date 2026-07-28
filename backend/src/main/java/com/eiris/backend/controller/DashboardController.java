package com.eiris.backend.controller;

import com.eiris.backend.dto.response.AdminDashboardResponse;
import com.eiris.backend.dto.response.AgencyDashboardResponse;
import com.eiris.backend.entity.User;
import com.eiris.backend.repository.UserRepository;
import com.eiris.backend.security.JwtUtil;
import com.eiris.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboardMetrics(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(dashboardService.getAdminDashboardMetrics());
    }

    @GetMapping("/agency/dashboard")
    public ResponseEntity<AgencyDashboardResponse> getAgencyDashboardMetrics(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(actualToken);
        User agencyUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(dashboardService.getAgencyDashboardMetrics(agencyUser));
    }
}
