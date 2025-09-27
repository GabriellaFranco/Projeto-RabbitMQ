package com.enterprise.suporte.dto.customer;

import com.enterprise.suporte.enuns.UserProfile;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerRequestDTO(

        @NotBlank
        @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Apenas letras são permitidas neste campo")
        @Size(min = 6, max = 80, message = "O nome deve ter entre 6 e 80 caracteres")
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 20, message = "A senha deve conter entre 6 e 20 caracteres")
        String password,

        @NotBlank
        @Pattern(regexp = "^(\\+?\\d{2})\\s*\\(?(\\d{2})\\)?\\s*(\\d{4,5})[-\\s]?(\\d{4})$",
        message = "Por favor, insira seu número de telefone no formato: 55(XX) XXXXX-XXXX ou 55(XX) XXXX-XXXX")
        String phone,

        @NotNull
        UserProfile profile,

        @NotNull
        LocalDate createdAt
) {
}
