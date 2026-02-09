package com.iprody.leonidm.paymentserviceapp.dto;

import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
    UUID uuid,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    Instant createdAt) {
}
