package com.example.shop.orders.api;

import java.math.BigDecimal;

public record Order(long id, long userId, long productId, String productName, int quantity, BigDecimal total,
        long paymentId) {
}
