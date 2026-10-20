package com.example.shop.catalog.api;

import java.util.List;

import io.github.ilyaslabs.moduleswitch.Idempotent;
import io.github.ilyaslabs.moduleswitch.Param;

public interface CatalogApi {

    /** @throws io.github.ilyaslabs.moduleswitch.ModuleException.NotFound if there is no such product */
    @Idempotent
    Product getProduct(@Param("id") long id);

    /** Products in a category, or every product when {@code category} is null. */
    @Idempotent
    List<Product> searchProducts(@Param("category") String category);
}
