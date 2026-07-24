package com.eiris.backend.controller;

import com.eiris.backend.dto.request.AgencyOrderRequest;
import com.eiris.backend.dto.request.AgencySaleRequest;
import com.eiris.backend.dto.response.AgencyInventoryResponse;
import com.eiris.backend.dto.response.AgencyMetricsResponse;
import com.eiris.backend.dto.response.AgencySaleResponse;
import com.eiris.backend.security.CustomUserDetails;
import com.eiris.backend.service.AgencyInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agency")
public class AgencyInventoryController {

    private final AgencyInventoryService inventoryService;

    public AgencyInventoryController(AgencyInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // --- ORDERS (Purchasing from Admin) ---
    @GetMapping("/orders")
    public ResponseEntity<List<com.eiris.backend.dto.response.AgencyOrderResponse>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(inventoryService.getOrders(userDetails.getUser()));
    }

    @PostMapping("/orders")
    public ResponseEntity<Void> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AgencyOrderRequest request) {
        inventoryService.placeOrder(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{id}/receive")
    public ResponseEntity<Void> receiveOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable java.util.UUID id) {
        inventoryService.receiveOrder(id, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // --- INVENTORY ---
    @GetMapping("/inventory")
    public ResponseEntity<List<AgencyInventoryResponse>> getInventory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(inventoryService.getInventory(userDetails.getUser()));
    }

    @GetMapping("/inventory/metrics")
    public ResponseEntity<AgencyMetricsResponse> getInventoryMetrics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(inventoryService.getMetrics(userDetails.getUser()));
    }

    // --- SALES ---
    @PostMapping("/sales")
    public ResponseEntity<Void> recordSale(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AgencySaleRequest request) {
        inventoryService.recordSale(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sales")
    public ResponseEntity<List<AgencySaleResponse>> getSales(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(inventoryService.getSales(userDetails.getUser()));
    }
}
