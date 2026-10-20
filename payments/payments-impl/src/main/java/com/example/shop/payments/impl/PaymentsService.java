package com.example.shop.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.example.shop.payments.api.Payment;
import com.example.shop.payments.api.PaymentsApi;
import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.stereotype.Service;

@Service
class PaymentsService implements PaymentsApi {

    private final Map<Long, BigDecimal> wallets = new ConcurrentHashMap<>(Map.of(
            1L, new BigDecimal("500.00"), 2L, new BigDecimal("50.00"), 3L, new BigDecimal("1000.00")));

    private final List<Payment> payments = new ArrayList<>();

    private final AtomicLong ids = new AtomicLong();

    @Override
    public synchronized Payment charge(long userId, BigDecimal amount, String reference) {
        if (amount == null || amount.signum() <= 0) {
            throw new ModuleException.BadRequest("Amount must be positive", "INVALID_AMOUNT");
        }
        BigDecimal balance = getBalance(userId);
        if (balance.compareTo(amount) < 0) {
            throw new ModuleException.Conflict("User " + userId + " has only " + balance + " in their wallet",
                    "INSUFFICIENT_FUNDS", Map.of("userId", userId, "balance", balance, "amount", amount));
        }
        this.wallets.put(userId, balance.subtract(amount));
        Payment payment = new Payment(this.ids.incrementAndGet(), userId, amount, reference);
        this.payments.add(payment);
        return payment;
    }

    @Override
    public BigDecimal getBalance(long userId) {
        return this.wallets.getOrDefault(userId, BigDecimal.ZERO);
    }

    @Override
    public synchronized List<Payment> listPayments() {
        return List.copyOf(this.payments);
    }
}
