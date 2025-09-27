package com.enterprise.suporte.dto.customer;

import com.enterprise.suporte.enuns.TicketStatus;
import com.enterprise.suporte.enuns.UserProfile;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate createdAt,
        Boolean isActive,
        List<TicketDTO> tickets
) {

    @Builder
    public record TicketDTO(
            Long id,
            String title,
            TicketStatus status
    ) {}
}
