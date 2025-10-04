package com.enterprise.suporte.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailDTO(

        @NotBlank
        String currentEmail,

        @NotBlank
        String newEmail
) {
}
