package com.enterprise.suporte.dto.supportagent;

import com.enterprise.suporte.enuns.UserProfile;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SupportAgentRequestDTO(

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

        @NotNull
        @Pattern(regexp = "^[0-9]+$", message = "Apenas números são permitidos neste campo")
        @Positive(message = "A capacidade máxima deve ser um valor positivo")
        int maxCapacity
) {
}
