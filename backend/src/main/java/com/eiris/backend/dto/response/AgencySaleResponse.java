package com.eiris.backend.dto.response;


import java.time.LocalDateTime;
import java.util.UUID;

public class AgencySaleResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String category;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String customerName;
    private LocalDateTime date;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
