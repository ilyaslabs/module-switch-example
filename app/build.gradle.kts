plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("io.github.ilyaslabs:module-switch-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // The app ships every module. Which ones actually load is decided by configuration.
    listOf("users", "catalog", "inventory", "payments", "orders").forEach { module ->
        implementation(project(":$module:$module-impl"))
    }

    testImplementation("io.github.ilyaslabs:module-switch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName = "shop.jar"
}
