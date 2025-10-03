package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.tickethistory.TicketHistoryResponseDTO;
import com.enterprise.suporte.model.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryMapper {

    public TicketHistoryResponseDTO toTicketHistoryResponseDTO(TicketHistory ticketHistory) {
        return TicketHistoryResponseDTO.builder()
                .ticketId(ticketHistory.getTicket().getId())
                .currentStatus(ticketHistory.getCurrentStatus())
                .updatedAt(ticketHistory.getUpdatedAt())
                .build();
    }
}
