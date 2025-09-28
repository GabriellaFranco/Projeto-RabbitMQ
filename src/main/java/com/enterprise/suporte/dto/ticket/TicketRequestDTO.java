package com.enterprise.suporte.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TicketRequestDTO(

        @NotBlank
        @Size(min = 10, max = 40, message = "O título deve ter entre 10 e 40 caracteres")
        String title,

        @NotBlank
        @Size(min = 50, max = 500, message = "A descrição deve ter entre 50 e 500 caracteres")
        String description


        ) {
}
