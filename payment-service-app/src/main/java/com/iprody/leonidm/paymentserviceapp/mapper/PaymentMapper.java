package com.iprody.leonidm.paymentserviceapp.mapper;

import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

public final class PaymentMapper {

    private PaymentMapper() {

    }

    public static Payment toEntity(PaymentDto model) {
        if (model == null) {
            return new Payment();
        }

        final Payment payment = new Payment();
        payment.setAmount(model.amount());
        payment.setCurrency(model.currency());
        payment.setCreatedAt(model.createdAt().atOffset(ZoneOffset.UTC));
        payment.setStatus(model.status());

        return payment;
    }

    public static PaymentDto toModel(Payment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Cannot convert null to PaymentDto");
        }

        return new PaymentDto(entity.getGuid(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt().toInstant());
    }

    public static List<PaymentDto> toModel(Collection<Payment> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(PaymentMapper::toModel)
                .toList();
    }
}
