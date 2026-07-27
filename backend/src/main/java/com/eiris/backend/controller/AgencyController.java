package com.eiris.backend.controller;

import com.eiris.backend.dto.request.CreateAgencyRequest;
import com.eiris.backend.dto.request.UpdateAgencyRequest;
import com.eiris.backend.dto.response.AgencyResponse;
import com.eiris.backend.dto.response.AgencyInventoryResponse;
import com.eiris.backend.dto.response.AgencyMetricsResponse;
import com.eiris.backend.dto.response.AgencySaleResponse;
import com.eiris.backend.dto.AgencyClientResponse;
import com.eiris.backend.service.AgencyService;
import com.eiris.backend.service.AgencyClientService;
import com.eiris.backend.service.AgencyInventoryService;
import com.eiris.backend.entity.User;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/agencies")
public class AgencyController {

    private final AgencyService agencyService;
    private final AgencyClientService agencyClientService;
    private final AgencyInventoryService agencyInventoryService;

    public AgencyController(AgencyService agencyService, AgencyClientService agencyClientService, AgencyInventoryService agencyInventoryService) {
        this.agencyService = agencyService;
        this.agencyClientService = agencyClientService;
        this.agencyInventoryService = agencyInventoryService;
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

    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getAgencyById(@PathVariable UUID id) {
        return ResponseEntity.ok(agencyService.getAgencyById(id));
    }

    @GetMapping("/{id}/clients")
    public ResponseEntity<List<AgencyClientResponse>> getAgencyClients(@PathVariable UUID id) {
        User user = agencyService.getAgencyUser(id);
        return ResponseEntity.ok(agencyClientService.getClientsForAgency(user));
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<AgencyInventoryResponse>> getAgencyInventory(@PathVariable UUID id) {
        User user = agencyService.getAgencyUser(id);
        return ResponseEntity.ok(agencyInventoryService.getInventory(user));
    }

    @GetMapping("/{id}/metrics")
    public ResponseEntity<AgencyMetricsResponse> getAgencyMetrics(@PathVariable UUID id) {
        User user = agencyService.getAgencyUser(id);
        return ResponseEntity.ok(agencyInventoryService.getMetrics(user));
    }

    @GetMapping("/{id}/sales")
    public ResponseEntity<List<AgencySaleResponse>> getAgencySales(@PathVariable UUID id) {
        User user = agencyService.getAgencyUser(id);
        return ResponseEntity.ok(agencyInventoryService.getSales(user));
    }
}
