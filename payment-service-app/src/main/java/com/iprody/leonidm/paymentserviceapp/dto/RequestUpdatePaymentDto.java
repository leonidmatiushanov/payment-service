package com.iprody.leonidm.paymentserviceapp.dto;

import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RequestUpdatePaymentDto(
    BigDecimal amount,
    String currency,
    UUID inquiryRefId,
    PaymentStatus status,
    String note,
    Instant createdAt
) {
}
