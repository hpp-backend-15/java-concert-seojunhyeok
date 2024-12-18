plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.hhp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Swagger and OpenAPI dependencies
    implementation("io.swagger.core.v3:swagger-annotations:2.2.10") // Latest Swagger annotations version
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0") // SpringDoc for OpenAPI

    // Test Container
    testImplementation("io.zonky.test:embedded-database-spring-test:2.5.1")

    // retryable
    implementation("org.springframework.retry:spring-retry")

    //redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Test Container dependencies for Redis
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
