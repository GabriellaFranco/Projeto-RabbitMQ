package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.notification.NotificationResponseDTO;
import com.enterprise.suporte.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toNotificationResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .ticketId(notification.getTicket().getId())
                .channel(notification.getChannel())
                .destiny(notification.getDestiny())
                .status(notification.getStatus())
                .message(notification.getMessage())
                .build();
    }
}
