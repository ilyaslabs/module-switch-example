package com.example.shop.inventory.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.shop.inventory.api.InventoryApi;
import com.example.shop.inventory.api.Stock;
import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.stereotype.Service;

@Service
class InventoryService implements InventoryApi {

    private final Map<Long, Integer> stock = new ConcurrentHashMap<>(Map.of(1L, 5, 2L, 10, 3L, 100, 4L, 0));

    @Override
    public Stock getStock(long productId) {
        return new Stock(productId, this.stock.getOrDefault(productId, 0));
    }

    @Override
    public List<Stock> listStock() {
        return this.stock.keySet().stream().sorted().map(this::getStock).toList();
    }

    @Override
    public synchronized Stock reserve(long productId, int quantity) {
        requirePositive(quantity);
        int available = this.stock.getOrDefault(productId, 0);
        if (available < quantity) {
            throw new ModuleException.Conflict("Only " + available + " left of product " + productId,
                    "OUT_OF_STOCK", Map.of("productId", productId, "available", available, "requested", quantity));
        }
        this.stock.put(productId, available - quantity);
        return getStock(productId);
    }

    @Override
    public synchronized Stock release(long productId, int quantity) {
        requirePositive(quantity);
        this.stock.merge(productId, quantity, Integer::sum);
        return getStock(productId);
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new ModuleException.BadRequest("Quantity must be at least 1", "INVALID_QUANTITY",
                    Map.of("quantity", quantity));
        }
    }
}
