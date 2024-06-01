package com.playkuround.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HealthCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthCheckApplication.class, args);
    }

}
