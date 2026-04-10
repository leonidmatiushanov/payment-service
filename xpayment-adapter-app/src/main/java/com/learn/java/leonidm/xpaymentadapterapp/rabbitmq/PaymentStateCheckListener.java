package com.learn.java.leonidm.xpaymentadapterapp.rabbitmq;

import com.learn.java.leonidm.xpaymentadapterapp.model.dto.PaymentCheckStateMessage;
import com.learn.java.leonidm.xpaymentadapterapp.service.PaymentStatusCheckHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.learn.java.leonidm.xpaymentadapterapp.config.RabbitMqDlxConfig.DEAD_LETTER_EXCHANGE;
import static com.learn.java.leonidm.xpaymentadapterapp.config.RabbitMqDlxConfig.DEAD_LETTER_ROUTING_KEY;

@Component
public class PaymentStateCheckListener {
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;
    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;
    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange-name}") String exchangeName,
            @Value("${app.rabbitmq.queue-name}") String routingKey,
            PaymentStatusCheckHandler paymentStatusCheckHandler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        MessageProperties props = raw.getMessageProperties();
        int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);
        boolean paid = paymentStatusCheckHandler.handle(message.chargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
            // Планируем следующую проверку
            PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(
                    message.chargeGuid(),
                    message.paymentGuid(),
                    message.amount(),
                    message.currency()
            );

            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKey,
                    newMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-delay", intervalMs);
                        m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                        return m;
                    }
            );
        } else {
            // Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(
                    DEAD_LETTER_EXCHANGE,
                    DEAD_LETTER_ROUTING_KEY,
                    message,
                    m -> {
                        m.getMessageProperties().setHeader("x-retry-count", retryCount);
                        m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                        m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                        return m;
                    }
            );
        }
    }
}
