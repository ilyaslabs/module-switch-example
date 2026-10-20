rootProject.name = "module-switch-example"

include("app")

// Five modules, each split into an -api part (interfaces + records) and an -impl part.
listOf("users", "catalog", "inventory", "payments", "orders").forEach { module ->
    include("$module:$module-api", "$module:$module-impl")
}
