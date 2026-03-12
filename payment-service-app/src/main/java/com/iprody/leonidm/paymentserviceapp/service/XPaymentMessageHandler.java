package com.iprody.leonidm.paymentserviceapp.service;

import com.iprody.leonidm.paymentserviceapp.async.MessageHandler;
import com.iprody.leonidm.paymentserviceapp.async.XPaymentAdapterResponseMessage;
import com.iprody.leonidm.paymentserviceapp.async.XPaymentAdapterStatus;
import com.iprody.leonidm.paymentserviceapp.persistence.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {
    private final PaymentRepository paymentRepository;

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        if (XPaymentAdapterStatus.SUCCEEDED == message.getStatus()) {
            paymentRepository.updateTransactionRefIdByGuid(message.getTransactionRefId(), message.getPaymentGuid());
        }
    }
}
