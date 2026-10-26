package com.example.shop;

import com.example.shop.orders.api.OrdersApi;
import com.example.shop.users.api.UsersApi;
import org.junit.jupiter.api.Test;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** What happens when you turn modules off. */
class ModuleModesTest {

    @Test
    void anOffModuleIsNotLoadedAtAll() {
        try (ConfigurableApplicationContext app = start("--module-switch.modules.orders.mode=off")) {
            assertThat(app.getBeanNamesForType(OrdersApi.class)).isEmpty();
            assertThat(app.getBean(UsersApi.class).getUser(1).name()).isEqualTo("Alice");
        }
    }

    @Test
    void turningOffAModuleThatOthersNeedStopsStartup() {
        assertThatThrownBy(() -> start("--module-switch.modules.payments.mode=off").close())
            .hasStackTraceContaining("PaymentsApi");
    }

    private static ConfigurableApplicationContext start(String... args) {
        String[] all = new String[args.length + 1];
        all[0] = "--server.port=0";
        System.arraycopy(args, 0, all, 1, args.length);
        return new SpringApplicationBuilder(ShopApplication.class).run(all);
    }
}
