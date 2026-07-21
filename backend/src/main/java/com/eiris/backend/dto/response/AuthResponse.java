package com.eiris.backend.dto.response;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserDto user
) {
    public record UserDto(
            UUID id,
            String email,
            String role
    ) {}
}
