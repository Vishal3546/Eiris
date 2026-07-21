package com.eiris.backend.service;

import com.eiris.backend.dto.request.CreateAgencyRequest;
import com.eiris.backend.dto.response.AgencyResponse;
import com.eiris.backend.entity.Agency;
import com.eiris.backend.entity.User;
import com.eiris.backend.repository.AgencyRepository;
import com.eiris.backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AgencyService(AgencyRepository agencyRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.agencyRepository = agencyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AgencyResponse createAgency(CreateAgencyRequest request) {
        // 1. Check if user email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("AGENCY");
        user = userRepository.save(user);

        Agency agency = new Agency();
        agency.setUser(user);
        agency.setAgencyName(request.getAgencyName());
        agency.setLocation(request.getLocation());
        agency.setContactNumber(request.getContactNumber());
        agency.setStatus("ACTIVE");
        agency = agencyRepository.save(agency);

        return mapToResponse(agency);
    }

    @Transactional(readOnly = true)
    public List<AgencyResponse> getAllAgencies() {
        return agencyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAgency(UUID agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new IllegalArgumentException("Agency not found"));
        // This will cascade delete the User due to DB constraints, but we should explicitly delete the User through JPA
        // Because Agency -> User is the relation direction. To delete User and have Agency deleted by cascade,
        // we should delete the User.
        userRepository.delete(agency.getUser());
    }

    private AgencyResponse mapToResponse(Agency agency) {
        AgencyResponse response = new AgencyResponse();
        response.setId(agency.getId());
        response.setAgencyName(agency.getAgencyName());
        response.setLocation(agency.getLocation());
        response.setContactNumber(agency.getContactNumber());
        response.setStatus(agency.getStatus());
        response.setEmail(agency.getUser().getEmail());
        response.setCreatedAt(agency.getCreatedAt());
        return response;
    }
}
