package com.enterprise.suporte.dto.ticket;

import com.enterprise.suporte.enuns.Channel;
import com.enterprise.suporte.enuns.NotificationStatus;
import com.enterprise.suporte.enuns.TicketPriority;
import com.enterprise.suporte.enuns.TicketStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TicketResponseDTO(
        Long id,
        Long customerId,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        AgentDTO agentResponsible,
        List<TicketHistoryDTO> history,
        List<NotificationDTO> notifications
) {

    @Builder
    public record AgentDTO(
            Long id,
            String name,
            String email
    ) {}

    @Builder
    public record TicketHistoryDTO(
            Long id,
            TicketStatus previousStatus,
            TicketStatus newStatus,
            LocalDateTime updatedAt
    ) {}

    @Builder
    public record NotificationDTO(
            Long id,
            Channel channel,
            String destiny,
            NotificationStatus status,
            String message,
            LocalDateTime sendIn
    ) {}
}
