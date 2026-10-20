package com.example.shop.users.impl;

import java.util.List;

import com.example.shop.users.api.User;
import com.example.shop.users.api.UsersApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public REST endpoints. Lives in the module, so it is only served where users is local. */
@RestController
@RequestMapping("/api/users")
class UsersController {

    private final UsersApi users;

    UsersController(UsersApi users) {
        this.users = users;
    }

    @GetMapping
    List<User> list() {
        return this.users.listUsers();
    }

    @GetMapping("/{id}")
    User get(@PathVariable long id) {
        return this.users.getUser(id);
    }
}
