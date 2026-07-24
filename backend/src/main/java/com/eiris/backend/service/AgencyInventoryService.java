package com.eiris.backend.service;

import com.eiris.backend.dto.request.AgencyOrderRequest;
import com.eiris.backend.dto.request.AgencySaleRequest;
import com.eiris.backend.dto.response.AgencyInventoryResponse;
import com.eiris.backend.dto.response.AgencyMetricsResponse;
import com.eiris.backend.dto.response.AgencyOrderResponse;
import com.eiris.backend.dto.response.AgencySaleResponse;
import com.eiris.backend.entity.*;
import com.eiris.backend.repository.AgencyInventoryRepository;
import com.eiris.backend.repository.AgencyOrderRepository;
import com.eiris.backend.repository.AgencySaleRepository;
import com.eiris.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgencyInventoryService {

    private final AgencyInventoryRepository inventoryRepository;
    private final AgencyOrderRepository orderRepository;
    private final AgencySaleRepository saleRepository;
    private final ProductRepository productRepository;

    public AgencyInventoryService(AgencyInventoryRepository inventoryRepository,
                                  AgencyOrderRepository orderRepository,
                                  AgencySaleRepository saleRepository,
                                  ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void placeOrder(User agencyUser, AgencyOrderRequest request) {
        for (AgencyOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            // 1. Create Order Record
            AgencyOrder order = new AgencyOrder();
            order.setAgencyUser(agencyUser);
            order.setProduct(product);
            order.setQuantity(item.getQuantity());
            order.setUnitPrice(product.getPrice());
            order.setTotalPrice(product.getPrice() * item.getQuantity());
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);
        }
    }

    @Transactional
    public void receiveOrder(java.util.UUID orderId, User agencyUser) {
        AgencyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getAgencyUser().getId().equals(agencyUser.getId())) {
            throw new IllegalArgumentException("Not authorized to receive this order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Order already received");
        }

        if (order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.APPROVED) {
            throw new IllegalArgumentException("Order is not shipped yet");
        }

        // Update status
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        // Update Inventory
        Product product = order.getProduct();
        AgencyInventory inventory = inventoryRepository.findByAgencyUserAndProduct(agencyUser, product)
                .orElseGet(() -> {
                    AgencyInventory newInv = new AgencyInventory();
                    newInv.setAgencyUser(agencyUser);
                    newInv.setProduct(product);
                    newInv.setAvailableQuantity(0);
                    return newInv;
                });

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + order.getQuantity());
        inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public List<AgencyOrderResponse> getOrders(User agencyUser) {
        return orderRepository.findByAgencyUserOrderByCreatedAtDesc(agencyUser).stream()
                .map(order -> {
                    AgencyOrderResponse resp = new AgencyOrderResponse();
                    resp.setId(order.getId());
                    resp.setProductName(order.getProduct().getName());
                    resp.setQuantity(order.getQuantity());
                    resp.setUnitPrice(order.getUnitPrice());
                    resp.setTotalPrice(order.getTotalPrice());
                    resp.setStatus(order.getStatus());
                    resp.setDate(order.getCreatedAt());
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgencyInventoryResponse> getInventory(User agencyUser) {
        return inventoryRepository.findByAgencyUser(agencyUser).stream()
                .map(inv -> {
                    AgencyInventoryResponse resp = new AgencyInventoryResponse();
                    resp.setId(inv.getProduct().getId());
                    resp.setName(inv.getProduct().getName());
                    resp.setCategory(inv.getProduct().getCategory());
                    resp.setPrice(inv.getProduct().getPrice());
                    resp.setImageUrl(inv.getProduct().getImageUrl());
                    resp.setQuantity(inv.getAvailableQuantity());
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgencyMetricsResponse getMetrics(User agencyUser) {
        List<AgencyInventory> inventory = inventoryRepository.findByAgencyUser(agencyUser);
        
        Integer totalItems = 0;
        Integer lowStockCount = 0;
        Integer outOfStockCount = 0;
        Double totalValue = 0.0;

        for (AgencyInventory inv : inventory) {
            int qty = inv.getAvailableQuantity();
            totalItems += qty;
            if (qty <= 0) {
                outOfStockCount++;
            } else if (qty <= 20) {
                lowStockCount++;
            }
            totalValue += inv.getProduct().getPrice() * qty;
        }

        AgencyMetricsResponse metrics = new AgencyMetricsResponse();
        metrics.setTotalItems(totalItems);
        metrics.setLowStockCount(lowStockCount);
        metrics.setOutOfStockCount(outOfStockCount);
        metrics.setTotalValue(totalValue);
        return metrics;
    }

    @Transactional
    public void recordSale(User agencyUser, AgencySaleRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        AgencyInventory inventory = inventoryRepository.findByAgencyUserAndProduct(agencyUser, product)
                .orElseThrow(() -> new IllegalArgumentException("Product not in inventory"));

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Deduct from inventory
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());
        inventoryRepository.save(inventory);

        // Record Sale
        AgencySale sale = new AgencySale();
        sale.setAgencyUser(agencyUser);
        sale.setProduct(product);
        sale.setCustomerName(request.getCustomerName());
        sale.setQuantity(request.getQuantity());
        sale.setUnitPrice(product.getPrice());
        sale.setTotalPrice(product.getPrice() * request.getQuantity());
        saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public List<AgencySaleResponse> getSales(User agencyUser) {
        return saleRepository.findByAgencyUserOrderByCreatedAtDesc(agencyUser).stream()
                .map(sale -> {
                    AgencySaleResponse resp = new AgencySaleResponse();
                    resp.setId(sale.getId());
                    resp.setProductId(sale.getProduct().getId());
                    resp.setProductName(sale.getProduct().getName());
                    resp.setCategory(sale.getProduct().getCategory());
                    resp.setQuantity(sale.getQuantity());
                    resp.setUnitPrice(sale.getUnitPrice());
                    resp.setTotalPrice(sale.getTotalPrice());
                    resp.setCustomerName(sale.getCustomerName());
                    resp.setDate(sale.getCreatedAt());
                    return resp;
                })
                .collect(Collectors.toList());
    }
}
