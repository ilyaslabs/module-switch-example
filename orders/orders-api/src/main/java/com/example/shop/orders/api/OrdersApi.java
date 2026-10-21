package com.example.shop.orders.api;

import java.util.List;

import io.github.ilyaslabs.moduleswitch.Idempotent;
import io.github.ilyaslabs.moduleswitch.Param;

public interface OrdersApi {

    /**
     * Checks the user and product, reserves stock and charges the user's wallet.
     * Fails with the error of whichever module said no.
     */
    Order placeOrder(@Param("userId") long userId, @Param("productId") long productId,
            @Param("quantity") int quantity);

    @Idempotent
    Order getOrder(@Param("id") long id);

    @Idempotent
    List<Order> listOrders();
}
