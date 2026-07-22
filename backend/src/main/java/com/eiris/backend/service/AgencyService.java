package com.eiris.backend.service;

import com.eiris.backend.dto.request.CreateAgencyRequest;
import com.eiris.backend.dto.request.UpdateAgencyRequest;
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
        agency.setRawPassword(request.getPassword());
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
    public AgencyResponse updateAgency(UUID agencyId, UpdateAgencyRequest request) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new IllegalArgumentException("Agency not found"));
        
        agency.setAgencyName(request.getAgencyName());
        agency.setLocation(request.getLocation());
        agency.setContactNumber(request.getContactNumber());
        agency.setRawPassword(request.getPassword());
        
        User user = agency.getUser();
        // Check if email changed and if the new email already exists
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use by another account");
            }
            user.setEmail(request.getEmail());
        }
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        agency = agencyRepository.save(agency);
        return mapToResponse(agency);
    }

    @Transactional
    public void deleteAgency(UUID agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new IllegalArgumentException("Agency not found"));
        
        agencyRepository.delete(agency);
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
        response.setRawPassword(agency.getRawPassword());
        response.setCreatedAt(agency.getCreatedAt());
        return response;
    }
}
