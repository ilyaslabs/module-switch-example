package com.example.shop;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.example.shop.inventory.api.InventoryApi;
import com.example.shop.orders.api.Order;
import com.example.shop.orders.api.OrdersApi;
import com.example.shop.payments.api.PaymentsApi;
import io.github.ilyaslabs.moduleswitch.ModuleException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** The default configuration: all five modules local, in one process. */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class MonolithTest {

    @Autowired
    private OrdersApi orders;

    @Autowired
    private InventoryApi inventory;

    @Autowired
    private PaymentsApi payments;

    @LocalServerPort
    private int port;

    @Test
    void placesAnOrderAcrossAllModules() {
        int stockBefore = this.inventory.getStock(2).available();

        Order order = this.orders.placeOrder(3, 2, 2);

        assertThat(order.productName()).isEqualTo("Coffee grinder");
        assertThat(order.total()).isEqualByComparingTo("179.00");
        assertThat(this.inventory.getStock(2).available()).isEqualTo(stockBefore - 2);
    }

    @Test
    void outOfStockIsAConflict() {
        assertThatThrownBy(() -> this.orders.placeOrder(1, 4, 1))
            .isInstanceOfSatisfying(ModuleException.Conflict.class,
                    (ex) -> assertThat(ex.code()).isEqualTo("OUT_OF_STOCK"));
    }

    @Test
    void failedPaymentPutsTheStockBack() {
        int stockBefore = this.inventory.getStock(1).available();
        BigDecimal balanceBefore = this.payments.getBalance(2);

        assertThatThrownBy(() -> this.orders.placeOrder(2, 1, 1))
            .isInstanceOfSatisfying(ModuleException.Conflict.class,
                    (ex) -> assertThat(ex.code()).isEqualTo("INSUFFICIENT_FUNDS"));

        assertThat(this.inventory.getStock(1).available()).isEqualTo(stockBefore);
        assertThat(this.payments.getBalance(2)).isEqualByComparingTo(balanceBefore);
    }

    @Test
    void restApiReportsModuleErrorsAsProblemDetails() throws Exception {
        HttpResponse<String> response = HttpClient.newHttpClient()
            .send(HttpRequest.newBuilder(URI.create("http://localhost:" + this.port + "/api/users/99")).build(),
                    HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("\"code\":\"USER_NOT_FOUND\"");
    }
}
