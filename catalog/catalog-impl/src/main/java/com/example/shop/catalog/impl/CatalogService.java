package com.example.shop.catalog.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.shop.catalog.api.CatalogApi;
import com.example.shop.catalog.api.Product;
import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.stereotype.Service;

@Service
class CatalogService implements CatalogApi {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    CatalogService() {
        save(new Product(1, "Espresso machine", "appliances", new BigDecimal("249.00")));
        save(new Product(2, "Coffee grinder", "appliances", new BigDecimal("89.50")));
        save(new Product(3, "Coffee beans 1kg", "groceries", new BigDecimal("18.90")));
        save(new Product(4, "Milk frother", "appliances", new BigDecimal("39.99")));
    }

    @Override
    public Product getProduct(long id) {
        return Optional.ofNullable(this.products.get(id))
            .orElseThrow(() -> new ModuleException.NotFound("Product " + id + " not found", "PRODUCT_NOT_FOUND",
                    Map.of("id", id)));
    }

    @Override
    public List<Product> searchProducts(String category) {
        return this.products.values()
            .stream()
            .filter((product) -> category == null || product.category().equalsIgnoreCase(category))
            .sorted(Comparator.comparingLong(Product::id))
            .toList();
    }

    private void save(Product product) {
        this.products.put(product.id(), product);
    }
}
