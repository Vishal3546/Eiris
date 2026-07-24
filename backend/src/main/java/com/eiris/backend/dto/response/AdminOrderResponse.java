package com.eiris.backend.dto.response;

import com.eiris.backend.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminOrderResponse {
    private UUID id;
    private String agencyName;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private OrderStatus status;
    private LocalDateTime date;

    public AdminOrderResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
