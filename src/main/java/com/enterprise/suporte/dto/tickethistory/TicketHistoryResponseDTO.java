package com.enterprise.suporte.dto.tickethistory;

import com.enterprise.suporte.enuns.TicketStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TicketHistoryResponseDTO(
        Long ticketId,
        TicketStatus currentStatus,
        LocalDateTime updatedAt
) {
}
