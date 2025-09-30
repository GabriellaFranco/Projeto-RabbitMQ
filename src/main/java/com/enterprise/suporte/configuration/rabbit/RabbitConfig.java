package com.enterprise.suporte.configuration.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TICKET_HISTORY_QUEUE = "ticket.history.queue";
    public static final String TICKET_HISTORY_EXCHANGE = "ticket.history.exchange";
    public static final String TICKET_HISTORY_ROUTING_KEY = "ticket.history";

    @Bean
    public Queue ticketHistoryQueue() {
        return new Queue(TICKET_HISTORY_QUEUE, true);
    }

    @Bean
    public DirectExchange ticketHistoryExchange() {
        return new DirectExchange(TICKET_HISTORY_EXCHANGE);
    }

    @Bean
    public Binding ticketHistoryBinding(Queue ticketHistoryQueue, DirectExchange ticketHistoryExchange) {
        return BindingBuilder
                .bind(ticketHistoryQueue)
                .to(ticketHistoryExchange)
                .with(TICKET_HISTORY_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
