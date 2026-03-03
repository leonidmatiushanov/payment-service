package com.iprody.leonidm.paymentserviceapp.mapper;

import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMapperTest {
    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapToDto() {
        //given
        Payment payment = buildPayment();

        //when
        PaymentDto dto = mapper.toDto(payment);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.guid()).isEqualTo(payment.getGuid());
        assertThat(dto.amount()).isEqualTo(payment.getAmount());
        assertThat(dto.currency()).isEqualTo(payment.getCurrency());

        assertThat(dto.status()).isEqualTo(payment.getStatus());
        assertThat(dto.createdAt()).isEqualTo(payment.getCreatedAt());
    }

    @Test
    void shouldMapToDtoCollection() {
        //given
        Payment payment = buildPayment();
        List<Payment> paymentList = List.of(payment);

        //when
        List<PaymentDto> dtoList = mapper.toDto(paymentList);

        //then
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).hasSize(1);
        PaymentDto dto = dtoList.getFirst();
        assertThat(dto).isNotNull();
        assertThat(dto.guid()).isEqualTo(payment.getGuid());
        assertThat(dto.amount()).isEqualTo(payment.getAmount());
        assertThat(dto.currency()).isEqualTo(payment.getCurrency());
        assertThat(dto.status()).isEqualTo(payment.getStatus());
        assertThat(dto.createdAt()).isEqualTo(payment.getCreatedAt());
    }

    @Test
    void shouldMapToEntity() {
        //given
        PaymentDto dto = buildPaymentDto();

        //when
        Payment entity = mapper.toEntity(dto);

        //then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(dto.guid());
        assertThat(entity.getAmount()).isEqualTo(dto.amount());
        assertThat(entity.getCurrency()).isEqualTo(dto.currency());
        assertThat(entity.getStatus()).isEqualTo(dto.status());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.createdAt());
    }

    private static PaymentDto buildPaymentDto() {
        return new PaymentDto(
                UUID.randomUUID(),
                new BigDecimal("999.99"),
                "EUR",
                PaymentStatus.PENDING,
                "some note",
                Instant.now()
        );
    }

    private static Payment buildPayment() {
        UUID guid = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setGuid(guid);
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency("USD");
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        return payment;
    }
}
