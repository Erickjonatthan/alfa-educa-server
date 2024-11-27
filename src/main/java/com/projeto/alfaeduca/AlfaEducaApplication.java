package com.projeto.alfaeduca;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.projeto.alfaeduca.config.AppConfig;

import java.io.IOException;

@SpringBootApplication
public class AlfaEducaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AlfaEducaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            AppConfig.loadApiKey();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API key", e);
        }
    }
}