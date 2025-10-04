package com.enterprise.suporte.dto.queue;

import com.enterprise.suporte.enuns.Channel;
import com.enterprise.suporte.enuns.TicketEvent;
import lombok.Builder;

@Builder
public record NotificationEventDTO(
        Long ticketId,
        Channel channel,
        String destiny,
        TicketEvent event,
        String message
) {
}
