package com.eiris.backend.controller;

import com.eiris.backend.dto.AgencyClientRequest;
import com.eiris.backend.dto.AgencyClientResponse;
import com.eiris.backend.entity.User;
import com.eiris.backend.service.AgencyClientService;
import com.eiris.backend.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agency/clients")
public class AgencyClientController {

    private final AgencyClientService agencyClientService;

    public AgencyClientController(AgencyClientService agencyClientService) {
        this.agencyClientService = agencyClientService;
    }

    @PostMapping
    public ResponseEntity<AgencyClientResponse> addClient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AgencyClientRequest request) {
        User user = userDetails.getUser();
        AgencyClientResponse response = agencyClientService.addClient(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AgencyClientResponse>> getClients(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<AgencyClientResponse> clients = agencyClientService.getClientsForAgency(user);
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyClientResponse> updateClient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody AgencyClientRequest request) {
        User user = userDetails.getUser();
        AgencyClientResponse response = agencyClientService.updateClient(id, user, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        User user = userDetails.getUser();
        agencyClientService.deleteClient(id, user);
        return ResponseEntity.noContent().build();
    }
}
