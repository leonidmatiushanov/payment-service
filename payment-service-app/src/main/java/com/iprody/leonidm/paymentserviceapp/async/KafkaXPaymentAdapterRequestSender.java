package com.iprody.leonidm.paymentserviceapp.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaXPaymentAdapterRequestSender implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> template;
    private final String topic;

    public KafkaXPaymentAdapterRequestSender(
        KafkaTemplate<String, XPaymentAdapterRequestMessage> template,
        @Value("${app.kafka.topics.xpayment-adapter.request:xpayment-adapter.requests}")
        String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage msg) {
        final String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency={} -> topic={}",
            msg.getPaymentGuid(), msg.getAmount(),
            msg.getCurrency(), topic);
        template.send(topic, key, msg);
    }
}