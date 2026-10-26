package com.example.shop.orders.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.example.shop.catalog.api.CatalogApi;
import com.example.shop.catalog.api.Product;
import com.example.shop.inventory.api.InventoryApi;
import com.example.shop.orders.api.Order;
import com.example.shop.orders.api.OrdersApi;
import com.example.shop.payments.api.Payment;
import com.example.shop.payments.api.PaymentsApi;
import com.example.shop.users.api.User;
import com.example.shop.users.api.UsersApi;
import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.stereotype.Service;

/**
 * Uses all four other modules through their APIs only. Whether each call stays in this process
 * or goes over HTTP is decided by configuration; this class never changes.
 */
@Service
class OrdersService implements OrdersApi {

    private final UsersApi users;

    private final CatalogApi catalog;

    private final InventoryApi inventory;

    private final PaymentsApi payments;

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    private final AtomicLong ids = new AtomicLong();

    OrdersService(UsersApi users, CatalogApi catalog, InventoryApi inventory, PaymentsApi payments) {
        this.users = users;
        this.catalog = catalog;
        this.inventory = inventory;
        this.payments = payments;
    }

    @Override
    public Order placeOrder(long userId, long productId, int quantity) {
        User user = this.users.getUser(userId);
        Product product = this.catalog.getProduct(productId);
        BigDecimal total = product.price().multiply(BigDecimal.valueOf(quantity));

        this.inventory.reserve(productId, quantity);
        Payment payment;
        try {
            payment = this.payments.charge(user.id(), total, "order for " + quantity + " x " + product.name());
        }
        catch (ModuleException ex) {
            this.inventory.release(productId, quantity);
            throw ex;
        }

        long id = this.ids.incrementAndGet();
        Order order = new Order(id, user.id(), product.id(), product.name(), quantity, total, payment.id());
        this.orders.put(id, order);
        return order;
    }

    @Override
    public Order getOrder(long id) {
        return Optional.ofNullable(this.orders.get(id))
            .orElseThrow(() -> new ModuleException.NotFound("Order " + id + " not found", "ORDER_NOT_FOUND",
                    Map.of("id", id)));
    }

    @Override
    public List<Order> listOrders() {
        return this.orders.values().stream().sorted(Comparator.comparingLong(Order::id)).toList();
    }
}
