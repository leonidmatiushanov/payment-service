package com.iprody.leonidm.paymentserviceapp.mapper;

import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentDto dto);

    List<PaymentDto> toDto(Collection<Payment> entities);
}
