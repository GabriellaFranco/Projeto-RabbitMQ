package com.enterprise.suporte.dto.supportagent;

import com.enterprise.suporte.enuns.AgentStatus;
import com.enterprise.suporte.enuns.TicketPriority;
import com.enterprise.suporte.enuns.TicketStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SupportAgentResponseDTO(
        Long id,
        String name,
        String email,
        AgentStatus status,
        Boolean isActive,
        LocalDate createdAt,
        List<TicketDTO> tickets
        ) {

    @Builder
    public record TicketDTO(
            Long id,
            String title,
            TicketPriority priority,
            TicketStatus status
    ) {}
}
