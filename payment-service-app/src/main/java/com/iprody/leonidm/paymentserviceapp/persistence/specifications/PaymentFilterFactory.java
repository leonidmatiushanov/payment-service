package com.iprody.leonidm.paymentserviceapp.persistence.specifications;

import com.iprody.leonidm.paymentserviceapp.dto.PaymentFilter;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PaymentFilterFactory {

    public static Specification<Payment> fromFilter(PaymentFilter filter) {
        Specification<Payment> spec = Specification.unrestricted();

        if (StringUtils.hasText(filter.currency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.currency()));
        }

        if (StringUtils.hasText(filter.status())) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.status()));
        }

        if (filter.minAmount() != null && filter.maxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.minAmount(), filter.maxAmount()));
        } else if (filter.minAmount() != null) {
            spec = spec.and(PaymentSpecifications.greaterThanAmountMin(filter.minAmount()));
        } else if (filter.maxAmount() != null) {
            spec = spec.and(PaymentSpecifications.lessThanAmountMax(filter.maxAmount()));
        }

        if (filter.createdAfter() != null && filter.createdBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.createdAfter(), filter.createdBefore()));
        } else if (filter.createdAfter() != null) {
            spec = spec.and(PaymentSpecifications.createdAfter(filter.createdAfter()));
        } else if (filter.createdBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBefore(filter.createdBefore()));
        }

        return spec;
    }
}
