package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping()
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentRepository.findById(guid).orElseThrow();
    }
}
