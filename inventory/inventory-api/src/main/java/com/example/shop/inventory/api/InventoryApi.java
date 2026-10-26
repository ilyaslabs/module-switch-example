package com.example.shop.inventory.api;

import java.util.List;

import io.github.ilyaslabs.moduleswitch.Idempotent;
import io.github.ilyaslabs.moduleswitch.Param;

public interface InventoryApi {

    @Idempotent
    Stock getStock(@Param("productId") long productId);

    @Idempotent
    List<Stock> listStock();

    /**
     * Takes {@code quantity} items out of stock.
     * @throws io.github.ilyaslabs.moduleswitch.ModuleException.Conflict with code {@code OUT_OF_STOCK}
     */
    Stock reserve(@Param("productId") long productId, @Param("quantity") int quantity);

    /** Puts reserved items back, for example when a payment fails. */
    Stock release(@Param("productId") long productId, @Param("quantity") int quantity);
}
