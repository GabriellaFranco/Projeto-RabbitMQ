package com.enterprise.suporte.dto.queue;

import com.enterprise.suporte.enuns.TicketEvent;
import lombok.Builder;

@Builder
public record TicketHistoryEventDTO(
        Long ticketId,
        TicketEvent event,
        String description,
        Long performedBy
) {
}
