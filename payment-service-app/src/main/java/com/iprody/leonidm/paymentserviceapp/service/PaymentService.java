package com.iprody.leonidm.paymentserviceapp.service;

import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.dto.PaymentFilter;
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

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;

    public List<PaymentDto> findAll() {
        final List<Payment> payments = repository.findAll();
        return payments.stream()
                .map(PaymentMapper::toModel)
                .toList();
    }

    public PaymentDto findById(UUID guid) {
        final Payment payment = repository.findById(guid).orElseThrow();
        return PaymentMapper.toModel(payment);
    }

    public List<PaymentDto> findByFilter(PaymentFilter filter) {
        final Specification<Payment> paymentSpecification = PaymentFilterFactory.fromFilter(filter);
        final List<Payment> payments = repository.findAll(paymentSpecification);
        return payments.stream()
                .map(PaymentMapper::toModel)
                .toList();
    }

    public Page<PaymentDto> findPaged(PaymentFilter filter, Pageable pageable) {
        final Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        final Page<Payment> payments = repository.findAll(spec, pageable);
        final List<PaymentDto> dtos = PaymentMapper.toModel(payments.getContent());
        return new PageImpl<>(dtos, pageable, payments.getTotalElements());
    }
}
