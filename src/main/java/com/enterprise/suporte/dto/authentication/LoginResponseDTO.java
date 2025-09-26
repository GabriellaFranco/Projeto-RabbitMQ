package com.enterprise.suporte.dto.authentication;

public record LoginResponseDTO(
        String status,
        String jwtToken
) {
}
