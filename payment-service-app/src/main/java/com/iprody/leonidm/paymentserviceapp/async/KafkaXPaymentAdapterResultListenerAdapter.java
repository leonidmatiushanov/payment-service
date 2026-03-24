package com.iprody.leonidm.paymentserviceapp.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaXPaymentAdapterResultListenerAdapter implements AsyncListener<XPaymentAdapterResponseMessage> {
    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    public KafkaXPaymentAdapterResultListenerAdapter(MessageHandler<XPaymentAdapterResponseMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterResponseMessage message) {
        handler.handle(message);
    }

    @KafkaListener(
        topics = "${app.kafka.topics.xpayment-adapter.response}",
        groupId = "${spring.kafka.consumer.group-id}")
    public void consume(
        XPaymentAdapterResponseMessage message,
        ConsumerRecord<String, XPaymentAdapterResponseMessage> record,
        Acknowledgment ack) {
        try {
            log.info("Received XPayment Adapter response: paymentGuid={}, status={}, partition={}, offset={}",
                message.getPaymentGuid(), message.getStatus(),
                record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter response for paymentGuid={}", message.getPaymentGuid(), e);
            throw e; // отдаём в error handler Spring Kafka
        }
    }
}