package com.enterprise.suporte.dto.user;

import com.enterprise.suporte.enuns.UserProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequestDTO(

        @NotBlank
        @Email
        String username,

        @NotBlank
        @Size(min = 6, max = 20, message = "A senha deve conter entre 6 e 20 caracteres")
        String password,

        @NotBlank
        UserProfile profile
) {
}
