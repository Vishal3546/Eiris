package com.eiris.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agency_sales")
public class AgencySale {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User agencyUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private AgencyClient client;

    @Column(name = "customer_name")
    private String customerName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public AgencySale() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getAgencyUser() { return agencyUser; }
    public void setAgencyUser(User agencyUser) { this.agencyUser = agencyUser; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public AgencyClient getClient() { return client; }
    public void setClient(AgencyClient client) { this.client = client; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
