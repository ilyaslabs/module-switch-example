package com.example.shop.catalog.impl;

import java.util.List;

import com.example.shop.catalog.api.CatalogApi;
import com.example.shop.catalog.api.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public REST endpoints. Lives in the module, so it is only served where catalog is local. */
@RestController
@RequestMapping("/api/products")
class CatalogController {

    private final CatalogApi catalog;

    CatalogController(CatalogApi catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    List<Product> search(@RequestParam(required = false) String category) {
        return this.catalog.searchProducts(category);
    }

    @GetMapping("/{id}")
    Product get(@PathVariable long id) {
        return this.catalog.getProduct(id);
    }
}
