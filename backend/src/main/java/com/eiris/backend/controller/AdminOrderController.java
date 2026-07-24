package com.eiris.backend.controller;

import com.eiris.backend.dto.response.AdminOrderResponse;
import com.eiris.backend.entity.OrderStatus;
import com.eiris.backend.service.AdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {
        adminOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
