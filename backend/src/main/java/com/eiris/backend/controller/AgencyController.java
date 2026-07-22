package com.eiris.backend.controller;

import com.eiris.backend.dto.request.CreateAgencyRequest;
import com.eiris.backend.dto.request.UpdateAgencyRequest;
import com.eiris.backend.dto.response.AgencyResponse;
import com.eiris.backend.service.AgencyService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/agencies")
public class AgencyController {

    private final AgencyService agencyService;

    public AgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @PostMapping
    public ResponseEntity<AgencyResponse> createAgency(@Valid @RequestBody CreateAgencyRequest request) {
        return ResponseEntity.ok(agencyService.createAgency(request));
    }

    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getAllAgencies() {
        return ResponseEntity.ok(agencyService.getAllAgencies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyResponse> updateAgency(@PathVariable UUID id, @Valid @RequestBody UpdateAgencyRequest request) {
        return ResponseEntity.ok(agencyService.updateAgency(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable UUID id) {
        agencyService.deleteAgency(id);
        return ResponseEntity.noContent().build();
    }
}
