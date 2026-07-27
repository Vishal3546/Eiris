package com.eiris.backend.service;

import com.eiris.backend.dto.AgencyClientRequest;
import com.eiris.backend.dto.AgencyClientResponse;
import com.eiris.backend.entity.User;

import java.util.List;

import java.util.UUID;

public interface AgencyClientService {
    AgencyClientResponse addClient(User user, AgencyClientRequest request);
    List<AgencyClientResponse> getClientsForAgency(User user);
    AgencyClientResponse updateClient(UUID clientId, User user, AgencyClientRequest request);
    void deleteClient(UUID clientId, User user);
}
