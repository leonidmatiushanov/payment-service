package com.iprody.leonidm.paymentserviceapp.async;

import org.springframework.stereotype.Component;

@Component
public class InMemoryXPaymentAdapterResultListenerAdapter implements AsyncListener<XPaymentAdapterResponseMessage> {
    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    public InMemoryXPaymentAdapterResultListenerAdapter(MessageHandler<XPaymentAdapterResponseMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterResponseMessage msg) {
        handler.handle(msg);
    }
}
