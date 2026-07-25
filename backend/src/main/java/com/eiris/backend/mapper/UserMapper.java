package com.eiris.backend.mapper;

import com.eiris.backend.dto.response.AuthResponse;
import com.eiris.backend.entity.User;
import com.eiris.backend.entity.Agency;
import com.eiris.backend.repository.AgencyRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final AgencyRepository agencyRepository;

    public UserMapper(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public AuthResponse.UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        String name = null;
        if ("AGENCY".equals(user.getRole())) {
            name = agencyRepository.findByUser(user)
                    .map(Agency::getAgencyName)
                    .orElse(user.getEmail());
        }

        return new AuthResponse.UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                name
        );
    }
}
