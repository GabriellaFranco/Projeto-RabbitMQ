package com.enterprise.suporte.listener;

import com.enterprise.suporte.configuration.rabbit.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DlqReprocess {

    private final AmqpTemplate amqpTemplate;

    public void reprocessTicketHistory(String message) {
        log.info("[DLQ Reprocess] Reenviando mensagem para ticket.history.queue");
        amqpTemplate.convertAndSend(
                RabbitConfig.TICKET_HISTORY_EXCHANGE,
                RabbitConfig.TICKET_HISTORY_ROUTING_KEY,
                message
        );
    }

    public void reprocessNotification(String message) {
        log.info("[DLQ Reprocess] Reenviando mensagem para notification.queue");
        amqpTemplate.convertAndSend(
                RabbitConfig.NOTIFICATION_EXCHANGE,
                RabbitConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
    }
}

