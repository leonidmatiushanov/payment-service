package com.learn.java.leonidm.xpaymentadapterapp.async;

import com.learn.java.leonidm.xpaymentadapterapp.service.validator.XPaymentAdapterRqAmountValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {
    private static final String DLT_SUFFIX = "-dlt";
    private final MessageHandler<XPaymentAdapterRequestMessage> handler;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public KafkaXPaymentAdapterRequestListenerAdapter(
        MessageHandler<XPaymentAdapterRequestMessage> handler,
        KafkaTemplate<String, String> kafkaTemplate,
        @Value("${app.kafka.topics.x-payment-adapter.request}") String topic
    ) {
        this.handler = handler;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.x-payment-adapter.request}",
        groupId = "${spring.kafka.consumer.group-id}")
    public void consume(
        ConsumerRecord<String, XPaymentAdapterRequestMessage> record,
        Acknowledgment ack
    ) {
        XPaymentAdapterRequestMessage message = record.value();
        boolean validMessage = XPaymentAdapterRqAmountValidator.validate(message.getAmount(), message.getCurrency());
        if (!validMessage) {
            log.error("Error validation XPayment Adapter request for paymentGuid={}", message.getPaymentGuid());
            kafkaTemplate.send(topic + DLT_SUFFIX, message.getPaymentGuid().toString(),"Ошибка валидации суммы и валюты");
            return;
        }

        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}", message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid={}", message.getPaymentGuid(), e);
            throw e;
        }
    }
}