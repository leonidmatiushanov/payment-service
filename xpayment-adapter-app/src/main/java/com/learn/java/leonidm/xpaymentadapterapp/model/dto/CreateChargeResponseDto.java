package com.learn.java.leonidm.xpaymentadapterapp.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateChargeResponseDto(
        UUID id,
        BigDecimal amount,
        String currency,
        BigDecimal amountReceived,
        String createdAt,
        String chargedAt,
        String customer,
        UUID order,
        String receiptEmail,
        String status
) {
}
