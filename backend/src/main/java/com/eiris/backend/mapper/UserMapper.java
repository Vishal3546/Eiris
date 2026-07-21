package com.eiris.backend.mapper;

import com.eiris.backend.dto.response.AuthResponse;
import com.eiris.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AuthResponse.UserDto toDto(User user);
}
