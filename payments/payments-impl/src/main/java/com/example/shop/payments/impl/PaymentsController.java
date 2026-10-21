package com.example.shop.payments.impl;

import java.math.BigDecimal;
import java.util.List;

import com.example.shop.payments.api.Payment;
import com.example.shop.payments.api.PaymentsApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public REST endpoints. Lives in the module, so it is only served where payments is local. */
@RestController
@RequestMapping("/api/payments")
class PaymentsController {

    private final PaymentsApi payments;

    PaymentsController(PaymentsApi payments) {
        this.payments = payments;
    }

    @GetMapping
    List<Payment> list() {
        return this.payments.listPayments();
    }

    @GetMapping("/balance/{userId}")
    BigDecimal balance(@PathVariable long userId) {
        return this.payments.getBalance(userId);
    }
}
