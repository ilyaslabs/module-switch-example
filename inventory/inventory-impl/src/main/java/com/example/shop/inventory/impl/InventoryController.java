package com.example.shop.inventory.impl;

import java.util.List;

import com.example.shop.inventory.api.InventoryApi;
import com.example.shop.inventory.api.Stock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public REST endpoints. Lives in the module, so it is only served where inventory is local. */
@RestController
@RequestMapping("/api/stock")
class InventoryController {

    private final InventoryApi inventory;

    InventoryController(InventoryApi inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    List<Stock> list() {
        return this.inventory.listStock();
    }

    @GetMapping("/{productId}")
    Stock get(@PathVariable long productId) {
        return this.inventory.getStock(productId);
    }
}
