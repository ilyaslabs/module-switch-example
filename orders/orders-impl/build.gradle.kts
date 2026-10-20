// orders calls the other four modules, so it depends on their APIs (never on their -impl parts).
dependencies {
    implementation(project(":users:users-api"))
    implementation(project(":catalog:catalog-api"))
    implementation(project(":inventory:inventory-api"))
    implementation(project(":payments:payments-api"))
}
