package com.eiris.backend.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import com.eiris.backend.entity.AgencyClient;

public class AgencyClientResponse {
    private UUID id;
    private String clientName;
    private String clientCompany;
    private String clientEmail;
    private String clientContact;
    private String clientState;
    private String clientCity;
    private Double sales;
    private Integer orders;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public AgencyClientResponse() {}

    public AgencyClientResponse(AgencyClient client) {
        this.id = client.getId();
        this.clientName = client.getClientName();
        this.clientCompany = client.getClientCompany();
        this.clientEmail = client.getClientEmail();
        this.clientContact = client.getClientContact();
        this.clientState = client.getClientState();
        this.clientCity = client.getClientCity();
        this.sales = client.getSales();
        this.orders = client.getOrders();
        this.createdAt = client.getCreatedAt();
        this.updatedAt = client.getUpdatedAt();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
