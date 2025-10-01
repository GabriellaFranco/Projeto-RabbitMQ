package com.enterprise.suporte.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationDlqListener {

    private final DlqReprocess dlqReprocess;

    @RabbitListener(queues = "notification.dlq.queue")
    public void consumeNotificationDlq(String message) {
        log.error("[DLQ] Mensagem não processada em notification.queue: {}", message);
        dlqReprocess.reprocessNotification(message);
    }
}
