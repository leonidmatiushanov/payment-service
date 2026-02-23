package com.iprody.leonidm.paymentserviceapp.service;

import com.iprody.leonidm.paymentserviceapp.dto.*;
import com.iprody.leonidm.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.leonidm.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;
import com.iprody.leonidm.paymentserviceapp.persistence.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @InjectMocks
    private PaymentService paymentService;

    private final Instant startDate = Instant.parse("2020-01-01T10:00:00.00Z");
    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();

        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        paymentDto = new PaymentDto(payment.getGuid(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getNote(),
                payment.getCreatedAt());
    }

    @Test
    void shouldReturnPaymentById() {
        //given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.findById(guid);

        //then
        assertEquals(guid, result.guid());
        assertEquals("USD", result.currency());
        assertEquals(PaymentStatus.APPROVED, result.status());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldReturnPaymentByFilter() {
        //given
        PaymentFilter paymentFilter = new PaymentFilter("USD",
                new BigDecimal(100),
                new BigDecimal(200),
                PaymentStatus.APPROVED.name(),
                Instant.now(),
                Instant.now()
        );
        List<Payment> paymentList = List.of(payment);
        Specification<Payment> anySpecification = Mockito.any();
        when(paymentRepository.findAll(anySpecification)).thenReturn(paymentList);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        List<PaymentDto> results = paymentService.findByFilter(paymentFilter);

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        PaymentDto result = results.getFirst();
        assertEquals(guid, result.guid());
        assertEquals("USD", result.currency());
        assertEquals(PaymentStatus.APPROVED, result.status());
        verify(paymentRepository).findAll(Mockito.any(Specification.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    public void shouldReturnPaymentByStatusFilter() {
        //given
        PaymentFilter paymentFilter = new PaymentFilter(null,
                new BigDecimal(0),
                null,
                PaymentStatus.APPROVED.name(),
                null,
                null
        );
        List<Payment> paymentList = buildListPayment(7).stream()
                .filter(payment1 -> PaymentStatus.APPROVED == payment1.getStatus())
                .toList();
        Specification<Payment> anySpecification = Mockito.any();
        when(paymentRepository.findAll(anySpecification)).thenReturn(paymentList);
        when(paymentMapper.toDto(Mockito.any(Payment.class))).thenReturn(paymentDto);

        //when
        List<PaymentDto> results = paymentService.findByFilter(paymentFilter);

        //then
        assertNotNull(results);
        assertEquals(3, results.size());
        PaymentDto result = results.getFirst();
        assertEquals(PaymentStatus.APPROVED, result.status());
        verify(paymentRepository).findAll(Mockito.any(Specification.class));
        verify(paymentMapper, Mockito.times(3)).toDto(Mockito.any(Payment.class));
    }

    @Test
    void shouldReturnPaymentByAmountFilter() {
        //given
        PaymentFilter paymentFilter = new PaymentFilter(null,
                new BigDecimal(0),
                new BigDecimal(5),
                PaymentStatus.APPROVED.name(),
                null,
                null
        );
        List<Payment> paymentList = buildListPayment(7).stream()
                .filter(payment1 -> payment1.getAmount().compareTo(BigDecimal.valueOf(0)) > 0
                        && payment1.getAmount().compareTo(BigDecimal.valueOf(5)) <= 0)
                .toList();
        Specification<Payment> anySpecification = Mockito.any();
        when(paymentRepository.findAll(anySpecification)).thenReturn(paymentList);
        when(paymentMapper.toDto(Mockito.any(Payment.class))).thenReturn(paymentDto);

        //when
        List<PaymentDto> results = paymentService.findByFilter(paymentFilter);

        //then
        assertNotNull(results);
        assertEquals(5, results.size());
        PaymentDto result = results.getFirst();
        verify(paymentRepository).findAll(Mockito.any(Specification.class));
        verify(paymentMapper, Mockito.times(5)).toDto(Mockito.any(Payment.class));
    }


    @Test
    void shouldReturnPaymentPage() {
        //given
        PaymentFilter paymentFilter = new PaymentFilter(null,
                new BigDecimal(0),
                new BigDecimal(10),
                PaymentStatus.APPROVED.name(),
                null,
                null
        );
        List<Payment> paymentList = buildListPayment(30).stream()
                .sorted(new CurrencyPaymentComparator())
                .toList();
        List<PaymentDto> paymentDtoList = buildListPaymentDto(30);
        PageImpl page = new PageImpl(paymentList);
        Specification<Payment> anySpecification = Mockito.any();
        Pageable pageableAny = Mockito.any();
        final Pageable pageable = PageRequest.of(0, 25, Sort.by("currency").ascending());
        when(paymentRepository.findAll(anySpecification, pageableAny)).thenReturn(page);
        when(paymentMapper.toDto(Mockito.any(Collection.class))).thenReturn(paymentDtoList);

        //when
        Page<PaymentDto> resultsPage = paymentService.findPaged(paymentFilter, pageable);

        //then
        assertNotNull(resultsPage);
        List<PaymentDto> results = resultsPage.getContent();
        assertNotNull(results);
        assertEquals(30, results.size());
        verify(paymentRepository).findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class));
        verify(paymentMapper, Mockito.times(1)).toDto(Mockito.any(Collection.class));
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        //given
        payment.setStatus(status);
        paymentDto = new PaymentDto(paymentDto.guid(),
                paymentDto.amount(),
                paymentDto.currency(),
                status,
                paymentDto.note(),
                paymentDto.createdAt());

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.findById(guid);

        //then
        assertEquals(status, result.status());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldSuccess_createPayment() {
        //given
        RequestCreatePaymentDto requestCreatePaymentDto = new RequestCreatePaymentDto(payment.getAmount(), payment.getInquiryRefId(), payment.getCurrency(), payment.getStatus());
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toEntity(any(RequestCreatePaymentDto.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.createPayment(requestCreatePaymentDto);

        //then
        assertEquals(guid, result.guid());
        assertEquals("USD", result.currency());
        assertEquals(PaymentStatus.APPROVED, result.status());
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toEntity(any(RequestCreatePaymentDto.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldSuccess_updatePayment() {
        //given
        RequestUpdatePaymentDto requestUpdatePaymentDto = new RequestUpdatePaymentDto(payment.getAmount(),
                payment.getCurrency(), payment.getInquiryRefId(), payment.getStatus(), payment.getNote(), payment.getCreatedAt());
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toEntity(any(RequestUpdatePaymentDto.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentDto);
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.TRUE);

        //when
        PaymentDto result = paymentService.updatePayment(guid, requestUpdatePaymentDto);

        //then
        assertEquals(guid, result.guid());
        assertEquals("USD", result.currency());
        assertEquals(PaymentStatus.APPROVED, result.status());

        verify(paymentRepository).existsById(guid);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toEntity(any(RequestUpdatePaymentDto.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldNotFoundEntityException_updatePayment() {
        //given
        RequestUpdatePaymentDto requestUpdatePaymentDto = new RequestUpdatePaymentDto(payment.getAmount(),
                payment.getCurrency(), payment.getInquiryRefId(), payment.getStatus(), payment.getNote(), payment.getCreatedAt());
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.FALSE);

        //when
        assertThatThrownBy(() -> paymentService.updatePayment(guid, requestUpdatePaymentDto))
        //then
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден: " + guid);
        verify(paymentRepository).existsById(guid);
    }

    @Test
    void shouldSuccess_deletePayment() {
        //given
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.TRUE);
        doNothing().when(paymentRepository).deleteById(guid);

        //when
        paymentService.delete(guid);

        //then
        verify(paymentRepository).existsById(guid);
        verify(paymentRepository).deleteById(guid);
    }

    @Test
    void shouldNotFoundEntityException_deletePayment() {
        //given
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.FALSE);

        //when
        assertThatThrownBy(() -> paymentService.delete(guid))
        //then
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден: " + guid);
        verify(paymentRepository).existsById(guid);
    }

    @Test
    void shouldSuccess_updateNotePayment() {
        //given
        RequestUpdateNotePaymentDto requestUpdateNotePaymentDto = new RequestUpdateNotePaymentDto(payment.getNote());
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.TRUE);
        doNothing().when(paymentRepository).updateNotePayment(guid, requestUpdateNotePaymentDto.note());

        //when
        paymentService.updateNotePayment(guid, requestUpdateNotePaymentDto);

        //then
        verify(paymentRepository).existsById(guid);
        verify(paymentRepository).updateNotePayment(guid, requestUpdateNotePaymentDto.note());
    }

    @Test
    void shouldNotFoundEntityException_updateNotePayment() {
        //given
        RequestUpdateNotePaymentDto requestUpdateNotePaymentDto = new RequestUpdateNotePaymentDto(payment.getNote());
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.FALSE);

        //when
        assertThatThrownBy(() -> paymentService.updateNotePayment(guid, requestUpdateNotePaymentDto))
        //then
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден: " + guid);
        verify(paymentRepository).existsById(guid);
        verify(paymentRepository, never()).updateNotePayment(guid, requestUpdateNotePaymentDto.note());
    }

    static PaymentStatus[] statusProvider() {
        return PaymentStatus.values();
    }

    private List<Payment> buildListPayment(int countPayment) {
        List<Payment> paymentList = new ArrayList<>();
        for (int i = 1; i <= countPayment; i++) {
            String currency = i % 2 == 0 ? "USD" : "RUB";
            PaymentStatus status = i % 2 == 0 ? PaymentStatus.APPROVED : PaymentStatus.PENDING;
            paymentList.add(buildPayment(BigDecimal.valueOf(i), currency, status, startDate.plus(i, ChronoUnit.DAYS)));
        }
        return paymentList;
    }

    Payment buildPayment(BigDecimal amount, String currency, PaymentStatus status, Instant createdAt) {
        Payment payment = new Payment();
        payment.setGuid(UUID.randomUUID());
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(status);
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(Instant.now());
        return payment;
    }

    private List<PaymentDto> buildListPaymentDto(int countPayment) {
        List<PaymentDto> paymentList = new ArrayList<>();
        for (int i = 1; i <= countPayment; i++) {
            String currency = i % 2 == 0 ? "USD" : "RUB";
            PaymentStatus status = i % 2 == 0 ? PaymentStatus.APPROVED : PaymentStatus.PENDING;
            paymentList.add(buildPaymentDto(BigDecimal.valueOf(i), currency, status, String.valueOf(i), startDate.plus(i, ChronoUnit.DAYS)));
        }
        return paymentList;
    }

    PaymentDto buildPaymentDto(BigDecimal amount, String currency, PaymentStatus status, String note, Instant createdAt) {
        return new PaymentDto(UUID.randomUUID(), amount, currency, status, note, createdAt);
    }

    static class CurrencyPaymentComparator implements Comparator<Payment> {

        public int compare(Payment a, Payment b) {
            return a.getCurrency().toUpperCase().compareTo(b.getCurrency().toUpperCase());
        }
    }
}
