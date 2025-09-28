package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.user.UserRequestDTO;
import com.enterprise.suporte.dto.user.UserResponseDTO;
import com.enterprise.suporte.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRequestDTO userDTO) {
        return User.builder()
                .username(userDTO.username())
                .password(userDTO.password())
                .profile(userDTO.profile())
                .build();
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userProfile(user.getProfile())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
