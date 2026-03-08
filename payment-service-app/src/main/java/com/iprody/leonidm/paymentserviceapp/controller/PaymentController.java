package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.dto.*;
import com.iprody.leonidm.paymentserviceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;


    @GetMapping()
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getPayments() {
        log.info("GET payments all");
        final List<PaymentDto> dtoList = paymentService.findAll();
        log.debug("Sending response List PaymentDto: {}", dtoList);
        return dtoList;
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        final PaymentDto dto = paymentService.findById(guid);
        log.debug("Sending response PaymentDto: {}", dto);
        return dto;
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getPaymentByFilter(@ModelAttribute PaymentFilter filter) {
        log.info("GET payment by filter: {}", filter);
        final List<PaymentDto> dtoList = paymentService.findByFilter(filter);
        log.debug("Sending response List PaymentDto: {}", dtoList);
        return dtoList;
    }

    @GetMapping("/page-search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> searchPagePayments(
        @ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "amount") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {
        log.info("GET page payments by filter: {}, page: {}, size: {}, sortBy: {}, direction: {},",
            filter, page, size, sortBy, direction);
        final Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        final Page<PaymentDto> dtoPage = paymentService.findPaged(filter, pageable);
        log.debug("Sending response Page PaymentDto: {}", dtoPage);
        return dtoPage;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto createPayment(@RequestBody RequestCreatePaymentDto dto) {
        log.info("POST create Payment by dto: {}", dto);
        final PaymentDto payment = paymentService.createPayment(dto);
        log.debug("Sending response PaymentDto: {}", payment);
        return payment;
    }

    @PutMapping("/{guid}")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updatePayment(@PathVariable UUID guid, @RequestBody RequestUpdatePaymentDto dto) {
        log.info("PUT update Payment by id: {}, dto: {}", guid, dto);
        final PaymentDto paymentDto = paymentService.updatePayment(guid, dto);
        log.debug("Sending response PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID guid) {
        log.info("DELETE Payment by id: {}", guid);
        paymentService.delete(guid);
        log.debug("Success delete payment by id: {}", guid);
    }

    @PatchMapping("/{guid}")
    @PreAuthorize("hasRole('admin')")
    public void updateNotePayment(@PathVariable UUID guid, @RequestBody RequestUpdateNotePaymentDto dto) {
        log.info("Patch update note payment by id: {}, dto: {}", guid, dto);
        paymentService.updateNotePayment(guid, dto);
        log.debug("Success update note payment by id: {}, dto: {}", guid, dto);
    }
}
