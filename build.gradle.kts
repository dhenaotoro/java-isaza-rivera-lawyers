plugins {
    id("java")
    id("org.springframework.boot") version "3.4.0" // ← Ajusta a la última estable
    id("io.spring.dependency-management") version "1.1.6" // ← Se encarga de administrar dependencias de Spring Boot automaticamente, no el desarrollador
}


group = "co.isazariveralawyers"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}

// Fuentes del proyecto estan bajo la ruta `src/app/main/...` en este espacio de trabajo
sourceSets {
    named("main") {
        java {
            setSrcDirs(listOf("src/app/main/java"))
        }
        resources {
            setSrcDirs(listOf("src/app/main/resources"))
        }
    }
    named("test") {
        java {
            setSrcDirs(listOf("src/app/test/java"))
        }
        resources {
            setSrcDirs(listOf("src/app/test/resources"))
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
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}


tasks.test {
    useJUnitPlatform()
}

// Garantiza que Spring Boot JAR tenga la clase principal configurada
tasks.named("bootJar") {
    this as org.springframework.boot.gradle.tasks.bundling.BootJar
    mainClass.set("com.isazariveralawyers.api.LegacyAdviceApiApplication")
}