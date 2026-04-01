package com.learn.java.leonidm.xpaymentadapterapp.service;

import com.learn.java.leonidm.xpaymentadapterapp.async.*;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender) {
        this.sender = sender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        scheduler.schedule(() -> {
            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            BigDecimal amount = message.getAmount();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(amount);
            responseMessage.setCurrency(message.getCurrency());
            XPaymentAdapterStatus status = amount != null && amount.remainder(TWO).compareTo(BigDecimal.ZERO) == 0 ?
                XPaymentAdapterStatus.SUCCEEDED : XPaymentAdapterStatus.CANCELED;
            responseMessage.setStatus(status);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(Instant.now());
            sender.send(responseMessage);
        }, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
