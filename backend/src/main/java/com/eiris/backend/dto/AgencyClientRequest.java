package com.eiris.backend.dto;

public class AgencyClientRequest {
    private String clientName;
    private String clientCompany;
    private String clientEmail;
    private String clientContact;
    private String clientState;
    private String clientCity;

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
}
