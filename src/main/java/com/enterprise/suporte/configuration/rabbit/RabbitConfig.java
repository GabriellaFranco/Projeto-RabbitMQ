package com.enterprise.suporte.configuration.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TICKET_HISTORY_QUEUE = "ticket.history.queue";
    public static final String TICKET_HISTORY_EXCHANGE = "ticket.history.exchange";
    public static final String TICKET_HISTORY_ROUTING_KEY = "ticket.history";

    public static final String TICKET_HISTORY_DLQ = "ticket.history.dlq.queue";
    public static final String TICKET_HISTORY_DLQ_EXCHANGE = "ticket.history.dlq.exchange";
    public static final String TICKET_HISTORY_DLQ_ROUTING_KEY = "ticket.history.dlq";

    @Bean
    public Queue ticketHistoryQueue() {
        return QueueBuilder.durable(TICKET_HISTORY_QUEUE)
                .withArgument("x-dead-letter-exchange", TICKET_HISTORY_DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TICKET_HISTORY_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange ticketHistoryExchange() {
        return new DirectExchange(TICKET_HISTORY_EXCHANGE);
    }

    @Bean
    public Binding ticketHistoryBinding(Queue ticketHistoryQueue, DirectExchange ticketHistoryExchange) {
        return BindingBuilder.bind(ticketHistoryQueue)
                .to(ticketHistoryExchange)
                .with(TICKET_HISTORY_ROUTING_KEY);
    }

    @Bean
    public Queue ticketHistoryDlq() {
        return QueueBuilder.durable(TICKET_HISTORY_DLQ).build();
    }

    @Bean
    public DirectExchange ticketHistoryDlqExchange() {
        return new DirectExchange(TICKET_HISTORY_DLQ_EXCHANGE);
    }

    @Bean
    public Binding ticketHistoryDlqBinding(Queue ticketHistoryDlq, DirectExchange ticketHistoryDlqExchange) {
        return BindingBuilder.bind(ticketHistoryDlq)
                .to(ticketHistoryDlqExchange)
                .with(TICKET_HISTORY_DLQ_ROUTING_KEY);
    }

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification";

    public static final String NOTIFICATION_DLQ_QUEUE = "notification.dlq.queue";
    public static final String NOTIFICATION_DLQ_EXCHANGE = "notification.dlq.exchange";
    public static final String NOTIFICATION_DLQ_ROUTING_KEY = "notification.dlq";

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Queue notificationDlqQueue() {
        return QueueBuilder.durable(NOTIFICATION_DLQ_QUEUE).build();
    }

    @Bean
    public TopicExchange notificationDlqExchange() {
        return new TopicExchange(NOTIFICATION_DLQ_EXCHANGE);
    }

    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlqQueue())
                .to(notificationDlqExchange())
                .with(NOTIFICATION_DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
