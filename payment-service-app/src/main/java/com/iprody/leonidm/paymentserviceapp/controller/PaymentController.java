package com.iprody.leonidm.paymentserviceapp.controller;


import com.iprody.leonidm.paymentserviceapp.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Map<Long, Payment> PAYMENTS = new HashMap<>();

    static {
        PAYMENTS.put(1L, new Payment(1L, 100.00));
        PAYMENTS.put(2L, new Payment(2L, 200.00));
        PAYMENTS.put(3L, new Payment(3L, 300.00));
    }

    @GetMapping()
    public List<Payment> getPayments() {
        return new ArrayList<>(PAYMENTS.values());
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable longa id) {
        Payment payment = PAYMENTS.get(id);
        if (payment == null) {
            // чтобы не отправлять null и 200 статус, выкидываю 500 ошибку, знаю что нужно 404, но
            // т.к. в задании не сказано про обработку ошибок, решил не делать лишнего
            throw new RuntimeException("Payment not found");
        }
        return payment;
    }
}
