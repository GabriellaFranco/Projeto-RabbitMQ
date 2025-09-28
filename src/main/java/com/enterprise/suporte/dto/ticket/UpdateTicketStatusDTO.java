package com.enterprise.suporte.dto.ticket;

import com.enterprise.suporte.enuns.TicketStatus;
import lombok.Builder;

@Builder
public record UpdateTicketStatusDTO(
        TicketStatus newStatus
) {
}
