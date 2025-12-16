plugins {
    java
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "net.zeotrope"
version = "0.0.1-SNAPSHOT"
description = "item_cache_j.webservice"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["flywayDbVersion"] = "11.12.0"
extra["postgresqlJdbcDriverVersion"] = "42.7.8"
extra["redisTestContainersVersion"] = "2.2.4"
extra["springBootVersion"] = "3.5.8"
extra["testContainersVersion"] = "1.21.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-cache:${property("springBootVersion")}")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")


    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Docker Compose Development
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")


    // Flyway database migration
    implementation("org.flywaydb:flyway-core:${property("flywayDbVersion")}")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:${property("flywayDbVersion")}")
    // Needs to be available at compile time for PGobject usage in code
    runtimeOnly("org.postgresql:postgresql:${property("postgresqlJdbcDriverVersion")}")

    // Required for Netty DNS resolution on macOS to prevent R2DBC hangs
    runtimeOnly("io.netty:netty-resolver-dns-native-macos::osx-aarch_64")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:${property("springBootVersion")}")

    // testcontainers
    testImplementation("org.testcontainers:junit-jupiter:${property("testContainersVersion")}")
    testImplementation("org.testcontainers:postgresql:${property("testContainersVersion")}")
    testImplementation("com.redis:testcontainers-redis:${property("redisTestContainersVersion")}")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
