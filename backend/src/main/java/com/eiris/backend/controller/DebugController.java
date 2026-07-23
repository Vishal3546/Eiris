package com.eiris.backend.controller;
import com.eiris.backend.entity.*;
import com.eiris.backend.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class DebugController {
    private final UserRepository userRepo;
    private final AgencyRepository agencyRepo;
    public DebugController(UserRepository userRepo, AgencyRepository agencyRepo) { this.userRepo = userRepo; this.agencyRepo = agencyRepo; }

    @GetMapping("/api/public/debug-agencies")
    public Map<String, Object> debug() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> agencies = new ArrayList<>();
        for(Agency a : agencyRepo.findAll()) {
            Map<String, Object> am = new HashMap<>();
            am.put("agency_id", a.getId());
            am.put("agency_name", a.getAgencyName());
            am.put("user_id_in_agency", a.getUser() != null ? a.getUser().getId() : null);
            agencies.add(am);
        }
        List<Map<String, Object>> users = new ArrayList<>();
        for(User u : userRepo.findAll()) {
            Map<String, Object> um = new HashMap<>();
            um.put("user_id", u.getId());
            um.put("email", u.getEmail());
            um.put("role", u.getRole());
            users.add(um);
        }
        map.put("agencies", agencies);
        map.put("users", users);
        return map;
    }
}
