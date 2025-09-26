package com.enterprise.suporte.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
