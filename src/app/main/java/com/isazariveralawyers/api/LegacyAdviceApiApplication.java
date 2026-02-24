package com.isazariveralawyers.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LegacyAdviceApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacyAdviceApiApplication.class, args);
    }
}