package com.learn.java.leonidm.xpaymentadapterapp.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentStateCheckRegistrar {
    void register(
            UUID chargeGuid,
            UUID paymentGuid,
            BigDecimal amount,
            String currency);
}
