package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.dto.PaymentFilter;
import com.iprody.leonidm.paymentserviceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping()
    public List<PaymentDto> getPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{guid}")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        return paymentService.findById(guid);
    }

    @GetMapping("/filter")
    public List<PaymentDto> getPaymentByFilter(@ModelAttribute PaymentFilter filter) {
        System.out.println("getPaymentByFilter");
        return paymentService.findByFilter(filter);
    }

    @GetMapping("/page-search")
    public Page<PaymentDto> searchPagePayments(
        @ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "amount") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.findPaged(filter, pageable);
    }
}
