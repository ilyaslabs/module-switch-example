package com.example.shop;

import java.io.IOException;
import java.net.ServerSocket;

import com.example.shop.catalog.api.CatalogApi;
import com.example.shop.inventory.api.InventoryApi;
import com.example.shop.orders.api.Order;
import com.example.shop.orders.api.OrdersApi;
import io.github.ilyaslabs.moduleswitch.ModuleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The warehouse and shop profiles started side by side, the same way you would run them as two
 * processes: orders, in the shop, reaches catalog and inventory over HTTP.
 */
class SplitDeploymentTest {

    private static ConfigurableApplicationContext warehouse;

    private static ConfigurableApplicationContext shop;

    @BeforeAll
    static void start() throws IOException {
        int internalPort = freePort();
        warehouse = new SpringApplicationBuilder(ShopApplication.class).profiles("warehouse")
            .run("--server.port=0", "--module-switch.expose.port=" + internalPort);
        shop = new SpringApplicationBuilder(ShopApplication.class).profiles("shop")
            .run("--server.port=0", "--module-switch.modules.catalog.url=http://localhost:" + internalPort,
                    "--module-switch.modules.inventory.url=http://localhost:" + internalPort);
    }

    @AfterAll
    static void stop() {
        shop.close();
        warehouse.close();
    }

    @Test
    void eachServiceLoadsOnlyItsOwnModules() {
        assertThat(warehouse.getBeanNamesForType(OrdersApi.class)).isEmpty();
        assertThat(shop.getBean(CatalogApi.class).toString()).contains("remote");
    }

    @Test
    void ordersCallsTheWarehouseOverHttp() {
        int stockBefore = warehouse.getBean(InventoryApi.class).getStock(3).available();

        Order order = shop.getBean(OrdersApi.class).placeOrder(1, 3, 4);

        assertThat(order.total()).isEqualByComparingTo("75.60");
        assertThat(warehouse.getBean(InventoryApi.class).getStock(3).available()).isEqualTo(stockBefore - 4);
    }

    @Test
    void remoteErrorsArriveAsTheSameExceptions() {
        assertThatThrownBy(() -> shop.getBean(OrdersApi.class).placeOrder(1, 42, 1))
            .isInstanceOfSatisfying(ModuleException.NotFound.class,
                    (ex) -> assertThat(ex.code()).isEqualTo("PRODUCT_NOT_FOUND"));
    }

    private static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
