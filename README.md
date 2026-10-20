# Module-Switch example: a five-module shop

A runnable Spring Boot shop built with [Module-Switch](https://github.com/ilyaslabs/module-switch). Clone it, run it, then change a module's `mode` to `off` or `http` and watch what happens. You never edit Java code to move a module between processes.

## What's inside

Five modules. Each one has an `-api` part (interfaces and records) and an `-impl` part (the code, plus its REST controller):

| Module | Does | REST endpoints |
|---|---|---|
| `users` | Knows the shop's customers | `GET /api/users`, `GET /api/users/{id}` |
| `catalog` | Knows the products and prices | `GET /api/products?category=`, `GET /api/products/{id}` |
| `inventory` | Tracks stock, reserves items | `GET /api/stock`, `GET /api/stock/{productId}` |
| `payments` | Holds wallets, charges users | `GET /api/payments`, `GET /api/payments/balance/{userId}` |
| `orders` | Places orders using **all four** modules above | `POST /api/orders`, `GET /api/orders`, `GET /api/orders/{id}` |

```
module-switch-example/
├─ app/                       Spring Boot main class, config profiles, tests
├─ users/      users-api/      users-impl/
├─ catalog/    catalog-api/    catalog-impl/
├─ inventory/  inventory-api/  inventory-impl/
├─ payments/   payments-api/   payments-impl/
└─ orders/     orders-api/     orders-impl/   (depends on the other four -api parts only)
```

Placing an order (`OrdersService`) checks the user, looks up the product, reserves stock and charges the wallet. If the payment fails, it puts the stock back.

Sample data:

- Users 1 Alice (500.00), 2 Bob (50.00), 3 Carol (1000.00). The amounts are wallet balances.
- Products 1 Espresso machine (249.00, 5 in stock), 2 Coffee grinder (89.50, 10), 3 Coffee beans (18.90, 100), 4 Milk frother (39.99, **0 in stock**).

## Requirements

- JDK 25
- Nothing else: the Gradle wrapper downloads everything, including Module-Switch from Maven Central.

## 1. Run it as a monolith

```bash
./gradlew :app:bootRun
```

The log shows every module's mode:

```
Module-Switch modules: catalog=local, inventory=local, orders=local, payments=local, users=local
```

Place an order:

```bash
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' \
     -d '{"userId": 3, "productId": 2, "quantity": 2}'
```

`requests.http` has every request ready to click in IntelliJ or VS Code.

## 2. Split it into two services

Same code, two processes. The **warehouse** runs `catalog` and `inventory` and serves them on internal port 9081. The **shop** runs `users`, `payments` and `orders`, and calls the warehouse over HTTP.

```bash
./gradlew :app:bootJar

# terminal 1
java -jar app/build/libs/shop.jar --spring.profiles.active=warehouse

# terminal 2
java -jar app/build/libs/shop.jar --spring.profiles.active=shop
```

```
[warehouse] Module-Switch modules: catalog=local, inventory=local, orders=off, payments=off, users=off
[warehouse] Module-Switch exposing [catalog, inventory] on internal port 9081
[shop]      Module-Switch modules: catalog=http, inventory=http, orders=local, payments=local, users=local
```

The same `curl` to port 8080 still places an order, but now `getProduct` and `reserve` travel to the warehouse. See [`application-warehouse.yaml`](app/src/main/resources/application-warehouse.yaml) and [`application-shop.yaml`](app/src/main/resources/application-shop.yaml) for the whole difference.

## 3. Things to try

Each one is a config change only.

| Try | How | What you'll see |
|---|---|---|
| Turn a module off | Add `--module-switch.modules.orders.mode=off` | orders is never loaded, so `/api/orders` is 404, while `/api/users` still works. |
| Turn off a module others need | Add `--module-switch.modules.payments.mode=off` | Startup stops with `OrdersService needs PaymentsApi, but module 'payments' is off.` and tells you how to fix it. |
| Get an error from another module | Order product 4 (out of stock) or let Bob (user 2) buy the espresso machine | A 409 with code `OUT_OF_STOCK` or `INSUFFICIENT_FUNDS`, the same in monolith and split mode |
| Stop the warehouse | In split mode, stop terminal 1, then place an order | `503 MODULE_UNAVAILABLE` after the retries run out. Start it again and the next order works. |
| Use a wrong token | Start the shop with `INTERNAL_TOKEN=wrong` | Orders fail with `INVALID_TOKEN`. The internal port rejects the call. |
| Make a typo | `--module-switch.modules.catalgo.mode=local` | Startup stops and lists the known module names |
| Watch the calls | `GET /actuator/metrics/module.switch.call` | Every module call, tagged with `module`, `method`, `mode` (`local` or `http`) and `outcome` |
| Move another module | Copy the profile files and pick a different split, for example payments on its own | Only YAML changes |

## 4. Tests

```bash
./gradlew build
```

| Test | Shows |
|---|---|
| `ModuleStructureTest` | `ModuleRules.verify(...)` checks every module rule without starting Spring |
| `MonolithTest` | All five modules local; orders, errors and the REST error format |
| `SplitDeploymentTest` | Warehouse and shop started side by side; orders crosses HTTP |
| `ModuleModesTest` | An `off` module isn't loaded; turning off a needed module stops startup |

## Using a different Module-Switch version

The version lives in `gradle.properties` (`moduleSwitchVersion`). To try a library change before it's published, run `./gradlew publishToMavenLocal -Pversion=<version>` in the library and set the same version here. A locally published build always wins.

## Learn more

The library's [developer guide](https://github.com/ilyaslabs/module-switch/blob/main/docs/developer-guide.md) walks through every feature used here.
