package com.projeto.gfinder.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private static String apiKey;

    public static void loadApiKey() throws IOException {
        String apiKeyFilePath = "C:/api-key.txt";
        apiKey = new String(Files.readAllBytes(Paths.get(apiKeyFilePath))).trim();
    }

    public static String getApiKey() {
        return apiKey;
    }
}