package com.example.banktoyproject;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableBatchProcessing // Spring Batch
public class BankToyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankToyProjectApplication.class, args);
    }

}
