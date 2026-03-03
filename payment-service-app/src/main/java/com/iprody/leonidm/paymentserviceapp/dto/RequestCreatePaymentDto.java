package com.iprody.leonidm.paymentserviceapp.dto;

import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestCreatePaymentDto(
    BigDecimal amount,
    UUID inquiryRefId,
    String currency,
    PaymentStatus status) {
}
