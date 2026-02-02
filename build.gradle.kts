plugins {
    id("java")
    id("org.springframework.boot") version "3.4.0" // ← ajusta a la última estable
    id("io.spring.dependency-management") version "1.1.6"
}


group = "co.danielhenao"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}

// Project sources are under `src/app/main/...` in this workspace
sourceSets {
    named("main") {
        java {
            setSrcDirs(listOf("src/app/main/java"))
        }
        resources {
            setSrcDirs(listOf("src/app/main/resources"))
        }
    }
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.33")
    implementation("org.flywaydb:flyway-mysql:10.20.1")


    // Jackson (incluido por web), Lombok opcional
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


tasks.test {
    useJUnitPlatform()
}

// Ensure Spring Boot JAR has the main class configured
tasks.named("bootJar") {
    this as org.springframework.boot.gradle.tasks.bundling.BootJar
    mainClass.set("com.isazariveralawyers.api.LegacyAdviceApiApplication")
}