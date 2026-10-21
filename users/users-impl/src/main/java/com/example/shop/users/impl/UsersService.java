package com.example.shop.users.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.shop.users.api.User;
import com.example.shop.users.api.UsersApi;
import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.stereotype.Service;

@Service
class UsersService implements UsersApi {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    UsersService() {
        save(new User(1, "Alice", "alice@example.com"));
        save(new User(2, "Bob", "bob@example.com"));
        save(new User(3, "Carol", "carol@example.com"));
    }

    @Override
    public User getUser(long id) {
        return Optional.ofNullable(this.users.get(id))
            .orElseThrow(() -> new ModuleException.NotFound("User " + id + " not found", "USER_NOT_FOUND",
                    Map.of("id", id)));
    }

    @Override
    public List<User> listUsers() {
        return this.users.values().stream().sorted((a, b) -> Long.compare(a.id(), b.id())).toList();
    }

    private void save(User user) {
        this.users.put(user.id(), user);
    }
}
