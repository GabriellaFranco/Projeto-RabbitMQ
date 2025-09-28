package com.enterprise.suporte.dto.user;

import com.enterprise.suporte.enuns.UserProfile;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponseDTO(
        Long id,
        String username,
        UserProfile userProfile,
        Boolean isActive,
        LocalDate createdAt
) {
}
