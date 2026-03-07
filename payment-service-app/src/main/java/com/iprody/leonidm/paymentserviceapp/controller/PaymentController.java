package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.dto.*;
import com.iprody.leonidm.paymentserviceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(PaymentController.BASE_REQUEST_MAPPING)
@RequiredArgsConstructor
public class PaymentController {
    public static final String BASE_REQUEST_MAPPING = "/payments";
    public static final String GUID_PATH = "/{guid}";
    public static final String GET_PAYMENTS_API = BASE_REQUEST_MAPPING;
    public static final String GET_PAYMENT_API = BASE_REQUEST_MAPPING + GUID_PATH;
    public static final String GET_PAYMENTS_BY_FILTER_API = BASE_REQUEST_MAPPING + "/filter";
    public static final String GET_PAGE_PAYMENTS_BY_FILTER_API = BASE_REQUEST_MAPPING + "/page-search";
    public static final String POST_CREATE_PAYMENT_API = BASE_REQUEST_MAPPING;
    public static final String PUT_UPDATE_PAYMENT_API = BASE_REQUEST_MAPPING + GUID_PATH;
    public static final String DELETE_DELETE_PAYMENT_API = BASE_REQUEST_MAPPING + GUID_PATH;
    public static final String PATCH_UPDATE_NOTE_PAYMENT_API = BASE_REQUEST_MAPPING + GUID_PATH;
            ;
    private final PaymentService paymentService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        return paymentService.findById(guid);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getPaymentByFilter(@ModelAttribute PaymentFilter filter) {
        return paymentService.findByFilter(filter);
    }

    @GetMapping("/page-search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
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
    @PreAuthorize("hasRole('admin')")
    public PaymentDto createPayment(@RequestBody RequestCreatePaymentDto dto) {
        return paymentService.createPayment(dto);
    }

    @PutMapping("/{guid}")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updatePayment(@PathVariable UUID guid, @RequestBody RequestUpdatePaymentDto dto) {
        return paymentService.updatePayment(guid, dto);
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID guid) {
        paymentService.delete(guid);
    }

    @PatchMapping("/{guid}")
    @PreAuthorize("hasRole('admin')")
    public void updateNotePayment(@PathVariable UUID guid, @RequestBody RequestUpdateNotePaymentDto dto) {
        paymentService.updateNotePayment(guid, dto);
    }
}
