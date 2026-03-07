package com.iprody.leonidm.paymentserviceapp.service;

import com.iprody.leonidm.paymentserviceapp.dto.*;
import com.iprody.leonidm.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.leonidm.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.repository.PaymentRepository;
import com.iprody.leonidm.paymentserviceapp.persistence.specifications.PaymentFilterFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {
    public static final String PAYMENT_NOT_FOUND = "Запрашиваемый ресурс не был найден в базе данных";
    public static final String FIND_BY_ID_PAYMENT_OPERATION = "findById";
    public static final String UPDATE_PAYMENT_OPERATION = "updatePayment";
    public static final String DELETE_PAYMENT_OPERATION = "deletePayment";
    public static final String UPDATE_NOTE_PAYMENT_OPERATION = "updateNotePayment";
    private final PaymentRepository repository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDto> findAll() {
        final List<Payment> payments = repository.findAll();
        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public PaymentDto findById(UUID guid) {
        final Payment payment = repository.findById(guid).orElseThrow(
            () -> new EntityNotFoundException(PAYMENT_NOT_FOUND, FIND_BY_ID_PAYMENT_OPERATION, guid)
        );
        return paymentMapper.toDto(payment);
    }

    public List<PaymentDto> findByFilter(PaymentFilter filter) {
        final Specification<Payment> paymentSpecification = PaymentFilterFactory.fromFilter(filter);
        final List<Payment> payments = repository.findAll(paymentSpecification);
        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public Page<PaymentDto> findPaged(PaymentFilter filter, Pageable pageable) {
        final Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        final Page<Payment> payments = repository.findAll(spec, pageable);
        final List<PaymentDto> dtos = paymentMapper.toDto(payments.getContent());
        return new PageImpl<>(dtos, pageable, payments.getTotalElements());
    }

    public PaymentDto createPayment(RequestCreatePaymentDto dto) {
        final Payment entity = paymentMapper.toEntity(dto);
        final Payment savedEntity = repository.save(entity);
        return paymentMapper.toDto(savedEntity);
    }

    @Transactional
    public PaymentDto updatePayment(UUID guid, RequestUpdatePaymentDto dto) {
        if (!repository.existsById(guid)) {
            throw new EntityNotFoundException(PAYMENT_NOT_FOUND, UPDATE_PAYMENT_OPERATION, guid);
        }
        final Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(guid);
        final Payment saved = repository.save(updated);
        return paymentMapper.toDto(saved);
    }

    @Transactional
    public void delete(UUID guid) {
        if (!repository.existsById(guid)) {
            throw new EntityNotFoundException(PAYMENT_NOT_FOUND, DELETE_PAYMENT_OPERATION, guid);
        }
        repository.deleteById(guid);
    }

    @Transactional
    public void updateNotePayment(UUID guid, RequestUpdateNotePaymentDto dto) {
        if (!repository.existsById(guid)) {
            throw new EntityNotFoundException(PAYMENT_NOT_FOUND, UPDATE_NOTE_PAYMENT_OPERATION, guid);
        }
        repository.updateNotePayment(guid, dto.note());
    }
}
