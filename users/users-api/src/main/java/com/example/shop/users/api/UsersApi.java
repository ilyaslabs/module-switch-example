package com.example.shop.users.api;

import java.util.List;

import io.github.ilyaslabs.moduleswitch.Idempotent;
import io.github.ilyaslabs.moduleswitch.Param;

public interface UsersApi {

    /** @throws io.github.ilyaslabs.moduleswitch.ModuleException.NotFound if there is no such user */
    @Idempotent
    User getUser(@Param("id") long id);

    @Idempotent
    List<User> listUsers();
}
