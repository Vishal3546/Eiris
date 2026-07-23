package com.eiris.backend.service;

import com.eiris.backend.dto.AgencyClientRequest;
import com.eiris.backend.dto.AgencyClientResponse;
import com.eiris.backend.entity.Agency;
import com.eiris.backend.entity.AgencyClient;
import com.eiris.backend.entity.User;
import com.eiris.backend.repository.AgencyClientRepository;
import com.eiris.backend.repository.AgencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgencyClientServiceImpl implements AgencyClientService {

    private final AgencyClientRepository agencyClientRepository;
    private final AgencyRepository agencyRepository;

    public AgencyClientServiceImpl(AgencyClientRepository agencyClientRepository, AgencyRepository agencyRepository) {
        this.agencyClientRepository = agencyClientRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    public AgencyClientResponse addClient(User user, AgencyClientRequest request) {
        Agency agency = agencyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Agency not found for the logged-in user"));

        AgencyClient client = new AgencyClient();
        client.setAgency(agency);
        client.setClientName(request.getClientName());
        client.setClientCompany(request.getClientCompany());
        client.setClientEmail(request.getClientEmail());
        client.setClientContact(request.getClientContact());
        client.setClientState(request.getClientState());
        client.setClientCity(request.getClientCity());

        client = agencyClientRepository.save(client);

        return new AgencyClientResponse(client);
    }

    @Override
    public List<AgencyClientResponse> getClientsForAgency(User user) {
        Agency agency = agencyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Agency not found for the logged-in user"));

        return agencyClientRepository.findByAgencyId(agency.getId())
                .stream()
                .map(AgencyClientResponse::new)
                .collect(Collectors.toList());
    }
}
