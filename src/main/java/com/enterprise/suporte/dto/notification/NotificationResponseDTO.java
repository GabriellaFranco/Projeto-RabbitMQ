package com.enterprise.suporte.dto.notification;

import com.enterprise.suporte.enuns.Channel;
import com.enterprise.suporte.enuns.NotificationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponseDTO(
        Long ticketId,
        Channel channel,
        String destiny,
        String message,
        NotificationStatus status,
        LocalDateTime sendAt
) {
}
