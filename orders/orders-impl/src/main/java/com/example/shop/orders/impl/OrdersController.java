package com.example.shop.orders.impl;

import java.util.List;

import com.example.shop.orders.api.Order;
import com.example.shop.orders.api.OrdersApi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Public REST endpoints. Lives in the module, so it is only served where orders is local. */
@RestController
@RequestMapping("/api/orders")
class OrdersController {

    private final OrdersApi orders;

    OrdersController(OrdersApi orders) {
        this.orders = orders;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Order place(@RequestBody PlaceOrderRequest request) {
        return this.orders.placeOrder(request.userId(), request.productId(), request.quantity());
    }

    @GetMapping
    List<Order> list() {
        return this.orders.listOrders();
    }

    @GetMapping("/{id}")
    Order get(@PathVariable long id) {
        return this.orders.getOrder(id);
    }

    record PlaceOrderRequest(long userId, long productId, int quantity) {
    }
}
