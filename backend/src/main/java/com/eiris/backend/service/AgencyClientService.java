package com.eiris.backend.service;

import com.eiris.backend.dto.AgencyClientRequest;
import com.eiris.backend.dto.AgencyClientResponse;
import com.eiris.backend.entity.User;

import java.util.List;

public interface AgencyClientService {
    AgencyClientResponse addClient(User user, AgencyClientRequest request);
    List<AgencyClientResponse> getClientsForAgency(User user);
}
