package com.eiris.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"agency-clients\"")
public class AgencyClient {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "agency_id", referencedColumnName = "id", nullable = false)
    private Agency agency;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_company")
    private String clientCompany;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_contact")
    private String clientContact;

    @Column(name = "client_state")
    private String clientState;

    @Column(name = "client_city")
    private String clientCity;

    @Column(name = "sales")
    private Double sales = 0.0;

    @Column(name = "orders")
    private Integer orders = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    public AgencyClient() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Agency getAgency() { return agency; }
    public void setAgency(Agency agency) { this.agency = agency; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientCompany() { return clientCompany; }
    public void setClientCompany(String clientCompany) { this.clientCompany = clientCompany; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getClientContact() { return clientContact; }
    public void setClientContact(String clientContact) { this.clientContact = clientContact; }
    public String getClientState() { return clientState; }
    public void setClientState(String clientState) { this.clientState = clientState; }
    public String getClientCity() { return clientCity; }
    public void setClientCity(String clientCity) { this.clientCity = clientCity; }
    public Double getSales() { return sales; }
    public void setSales(Double sales) { this.sales = sales; }
    public Integer getOrders() { return orders; }
    public void setOrders(Integer orders) { this.orders = orders; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
