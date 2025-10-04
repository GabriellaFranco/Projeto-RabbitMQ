package com.enterprise.suporte.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDTO(

        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
) {
}
