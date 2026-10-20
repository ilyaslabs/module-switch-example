plugins {
    id("org.springframework.boot") version "4.1.1" apply false
}

val moduleSwitchVersion: String by project
val springBootVersion: String by project

subprojects {
    // Skip the plain "users", "catalog", ... folders; they only group the -api and -impl parts.
    if (childProjects.isNotEmpty()) return@subprojects

    apply(plugin = "java-library")

    group = "com.example.shop"

    repositories {
        // A locally built Module-Switch (./gradlew publishToMavenLocal in the library) wins.
        mavenLocal {
            content { includeGroup("io.github.ilyaslabs") }
        }
        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            content { includeGroup("io.github.ilyaslabs") }
            mavenContent { snapshotsOnly() }
        }
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    dependencies {
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        "implementation"(platform("io.github.ilyaslabs:module-switch-bom:$moduleSwitchVersion"))
        "testImplementation"(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        "testImplementation"(platform("io.github.ilyaslabs:module-switch-bom:$moduleSwitchVersion"))
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    // Every -api part needs only the Module-Switch annotations: no Spring.
    if (name.endsWith("-api")) {
        dependencies {
            "api"("io.github.ilyaslabs:module-switch-api")
        }
    }

    // Every -impl part implements its own API and can serve REST endpoints.
    if (name.endsWith("-impl")) {
        dependencies {
            "api"(project(":${parent!!.name}:${parent!!.name}-api"))
            "implementation"("org.springframework.boot:spring-boot-starter-webmvc")
        }
    }
}
