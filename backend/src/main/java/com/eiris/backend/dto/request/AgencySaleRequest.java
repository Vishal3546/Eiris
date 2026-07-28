package com.eiris.backend.dto.request;

import java.util.UUID;

public class AgencySaleRequest {
    private UUID productId;
    private Integer quantity;
    private String customerName;
    private UUID clientId; // Added field to link sale to a specific client

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
}
