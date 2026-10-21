package com.example.shop.payments.api;

import java.math.BigDecimal;

public record Payment(long id, long userId, BigDecimal amount, String reference) {
}
