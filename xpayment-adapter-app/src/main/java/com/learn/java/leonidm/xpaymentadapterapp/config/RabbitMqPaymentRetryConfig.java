package com.learn.java.leonidm.xpaymentadapterapp.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.learn.java.leonidm.xpaymentadapterapp.config.RabbitMqDlxConfig.DEAD_LETTER_EXCHANGE;
import static com.learn.java.leonidm.xpaymentadapterapp.config.RabbitMqDlxConfig.DEAD_LETTER_ROUTING_KEY;


@Configuration
public class RabbitMqPaymentRetryConfig {
    @Value("${app.rabbitmq.queue-name}")
    private String queueName;
    @Value("${app.rabbitmq.exchange-name}")
    private String delayedExchangeName;

    @Bean
    public Queue xpaymentQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public CustomExchange delayedExchange() {
        return new CustomExchange(delayedExchangeName,
                "x-delayed-message", true, false, Map.of("x-delayed-type", "direct"));
    }

    @Bean
    public Binding queueBinding(Queue xpaymentQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(xpaymentQueue).to(delayedExchange).with(queueName).noargs();
    }
}
