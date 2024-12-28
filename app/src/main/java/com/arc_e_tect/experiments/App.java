package com.arc_e_tect.experiments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.arc_e_tect"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        log.info("Spring Boot application started successfully.");
    }
}
