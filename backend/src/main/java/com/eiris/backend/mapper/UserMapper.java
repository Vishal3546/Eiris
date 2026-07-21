package com.eiris.backend.mapper;

import com.eiris.backend.dto.response.AuthResponse;
import com.eiris.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AuthResponse.UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new AuthResponse.UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
