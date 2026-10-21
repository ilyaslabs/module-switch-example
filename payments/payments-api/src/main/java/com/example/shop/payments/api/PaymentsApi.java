package com.example.shop.payments.api;

import java.math.BigDecimal;
import java.util.List;

import io.github.ilyaslabs.moduleswitch.Idempotent;
import io.github.ilyaslabs.moduleswitch.Param;

public interface PaymentsApi {

    /**
     * Takes {@code amount} from the user's wallet.
     * @throws io.github.ilyaslabs.moduleswitch.ModuleException.Conflict with code {@code INSUFFICIENT_FUNDS}
     */
    Payment charge(@Param("userId") long userId, @Param("amount") BigDecimal amount,
            @Param("reference") String reference);

    @Idempotent
    BigDecimal getBalance(@Param("userId") long userId);

    @Idempotent
    List<Payment> listPayments();
}
