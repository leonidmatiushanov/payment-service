package com.learn.java.leonidm.xpaymentadapterapp.service;

import com.learn.java.leonidm.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.learn.java.leonidm.xpaymentadapterapp.async.*;
import com.learn.java.leonidm.xpaymentadapterapp.mapper.CreateChargeMapper;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeRequestDto;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.Instant;

@Component
@Slf4j
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGateway xPaymentProviderGateway;

    @Autowired
    public RequestMessageHandler(
            AsyncSender<XPaymentAdapterResponseMessage> asyncSender,
            XPaymentProviderGateway xPaymentProviderGateway, CreateChargeMapper createChargeMapper
    ) {
        this.asyncSender = asyncSender;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
            message.getPaymentGuid(), message.getAmount(), message.getCurrency());

        CreateChargeRequestDto createChargeRequestDto = new CreateChargeRequestDto(
                message.getAmount(),
                message.getCurrency(),
                null,
                message.getPaymentGuid(),
                null
        );

        try {
            CreateChargeResponseDto chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequestDto);

            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                chargeResponse.status());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(chargeResponse.order());
            responseMessage.setTransactionRefId(chargeResponse.id());
            responseMessage.setAmount(chargeResponse.amount());
            responseMessage.setCurrency(chargeResponse.currency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.status()));
            responseMessage.setOccurredAt(Instant.now());

            asyncSender.send(responseMessage);
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(Instant.now());

            asyncSender.send(responseMessage);
        }
    }
}
