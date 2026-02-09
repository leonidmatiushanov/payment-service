package com.iprody.leonidm.paymentserviceapp.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFilter(
    String currency,
    BigDecimal minAmount,
    BigDecimal maxAmount,
    String status,
    Instant createdAfter,
    Instant createdBefore) {
}
