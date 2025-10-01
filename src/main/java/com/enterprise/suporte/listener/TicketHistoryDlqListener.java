package com.enterprise.suporte.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TicketHistoryDlqListener {

    private final DlqReprocess dlqReprocess;

    @RabbitListener(queues = "ticket.history.dlq.queue")
    public void consumeTicketHistoryDlq(String message) {
        log.error("[DLQ] Mensagem não processada em ticket.history.queue: {}", message);
        dlqReprocess.reprocessNotification(message);
    }
}
