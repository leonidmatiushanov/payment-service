package com.learn.java.leonidm.xpaymentadapterapp.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateChargeRequestDto(
        BigDecimal amount,
        String currency,
        String customer,
        UUID order,
        String receiptEmail
) {}

