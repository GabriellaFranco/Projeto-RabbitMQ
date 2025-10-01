package com.enterprise.suporte.service;

import com.enterprise.suporte.configuration.rabbit.RabbitConfig;
import com.enterprise.suporte.dto.queue.NotificationEventDTO;
import com.enterprise.suporte.enuns.Channel;
import com.enterprise.suporte.enuns.NotificationStatus;
import com.enterprise.suporte.enuns.TicketEvent;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.model.Notification;
import com.enterprise.suporte.repository.NotificationRepository;
import com.enterprise.suporte.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TicketRepository ticketRepository;
    private final AmqpTemplate amqpTemplate;

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    @Transactional
    public void consume(NotificationEventDTO event) {
        var ticket = ticketRepository.findById(event.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado: " + event.ticketId()));

        var notification = Notification.builder()
                .ticket(ticket)
                .channel(event.channel())
                .destiny(event.destiny())
                .message(event.message())
                .status(NotificationStatus.PENDENTE)
                .sendIn(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);


        try {
            System.out.printf("📩 Enviando notificação [%s] para %s: %s%n",
                    event.channel(), event.destiny(), event.message());
            notification.setStatus(NotificationStatus.ENVIADO);

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.ERRO);
        }
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendNotification(Long ticketId, Channel channel, String destiny, TicketEvent event,
                                 String msg, NotificationStatus status) {

        var dto = NotificationEventDTO.builder()
                .ticketId(ticketId)
                .channel(channel)
                .destiny(destiny)
                .event(event)
                .message(msg)
                .build();

        amqpTemplate.convertAndSend(
                RabbitConfig.NOTIFICATION_EXCHANGE,
                RabbitConfig.NOTIFICATION_ROUTING_KEY,
                dto,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    return message;
                }
        );
    }


}
