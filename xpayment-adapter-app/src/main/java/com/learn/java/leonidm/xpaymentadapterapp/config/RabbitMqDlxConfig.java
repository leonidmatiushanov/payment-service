package com.learn.java.leonidm.xpaymentadapterapp.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqDlxConfig {
    public static final String DEAD_LETTER_EXCHANGE = "payments.dlx";
    public static final String DEAD_LETTER_ROUTING_KEY = "payments.dead";

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable("payments.dead.queue").build();
    }

    @Bean
    Binding dlxBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}
