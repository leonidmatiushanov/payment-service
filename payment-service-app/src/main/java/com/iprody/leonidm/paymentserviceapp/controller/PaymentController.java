package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.dto.*;
import com.iprody.leonidm.paymentserviceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto createPayment(@RequestBody RequestCreatePaymentDto dto) {
        return paymentService.createPayment(dto);
    }

    @PutMapping("/{guid}")
    public PaymentDto updatePayment(@PathVariable UUID guid, @RequestBody RequestUpdatePaymentDto dto) {
        return paymentService.updatePayment(guid, dto);
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID guid) {
        paymentService.delete(guid);
    }

    @PatchMapping("/{guid}")
    public void updateNotePayment(@PathVariable UUID guid, @RequestBody RequestUpdateNotePaymentDto dto) {
        paymentService.updateNotePayment(guid, dto);
    }
}
