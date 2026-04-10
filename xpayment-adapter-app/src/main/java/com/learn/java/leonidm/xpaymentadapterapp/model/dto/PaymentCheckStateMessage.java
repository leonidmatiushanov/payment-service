package com.learn.java.leonidm.xpaymentadapterapp.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCheckStateMessage(
        UUID chargeGuid,
        UUID paymentGuid,
        BigDecimal amount,
        String currency
) implements Serializable {
}
