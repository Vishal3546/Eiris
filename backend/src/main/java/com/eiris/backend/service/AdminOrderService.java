package com.eiris.backend.service;

import com.eiris.backend.dto.response.AdminOrderResponse;
import com.eiris.backend.entity.AgencyOrder;
import com.eiris.backend.entity.OrderStatus;
import com.eiris.backend.repository.AgencyOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminOrderService {

    private final AgencyOrderRepository orderRepository;

    public AdminOrderService(AgencyOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                // Sort by date descending (null-safe)
                .sorted((a, b) -> {
                    if (a.getDate() == null && b.getDate() == null) return 0;
                    if (a.getDate() == null) return 1;
                    if (b.getDate() == null) return -1;
                    return b.getDate().compareTo(a.getDate());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        AgencyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
                
        // Validation to prevent going backwards (simplistic approach for now)
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status of a completed/cancelled order");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private AdminOrderResponse mapToResponse(AgencyOrder order) {
        AdminOrderResponse resp = new AdminOrderResponse();
        resp.setId(order.getId());
        resp.setAgencyName(order.getAgencyUser().getEmail()); // Using email as identifier for now
        resp.setProductName(order.getProduct().getName());
        resp.setQuantity(order.getQuantity());
        resp.setUnitPrice(order.getUnitPrice());
        resp.setTotalPrice(order.getTotalPrice());
        resp.setStatus(order.getStatus());
        resp.setDate(order.getCreatedAt());
        return resp;
    }
}
