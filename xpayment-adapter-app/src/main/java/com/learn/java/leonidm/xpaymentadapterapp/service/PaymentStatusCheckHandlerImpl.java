package com.learn.java.leonidm.xpaymentadapterapp.service;

import com.learn.java.leonidm.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.learn.java.leonidm.xpaymentadapterapp.async.AsyncSender;
import com.learn.java.leonidm.xpaymentadapterapp.async.XPaymentAdapterResponseMessage;
import com.learn.java.leonidm.xpaymentadapterapp.async.XPaymentAdapterStatus;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    public PaymentStatusCheckHandlerImpl(XPaymentProviderGateway xPaymentProviderGateway, AsyncSender<XPaymentAdapterResponseMessage> asyncSender) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.asyncSender = asyncSender;
    }

    @Override
    public boolean handle(UUID paymentGuid) {
        log.info("Checking payment status for payment {}", paymentGuid);
        CreateChargeResponseDto createChargeRsDto = xPaymentProviderGateway.retrieveCharge(paymentGuid);
        String status = createChargeRsDto.status();
        if ("SUCCEEDED".equals(status) || "CANCELED".equals(status)) {
            // отправка уведомления в kafka об измении статуса платежа на один из терминальных
            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(paymentGuid);
            responseMessage.setAmount(createChargeRsDto.amount());
            responseMessage.setCurrency(createChargeRsDto.currency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(status));
            responseMessage.setOccurredAt(Instant.now());

            asyncSender.send(responseMessage);
            return true;
        }
        return false;
    }
}
