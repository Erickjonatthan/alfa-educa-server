package com.projeto.alfaeduca.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.projeto.alfaeduca.usuario.email.EmailVerifier;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean 
    public OCR ocr() {
        return new OCR();
    }

    @Bean
    public EmailVerifier emailVerifier() {
        return new EmailVerifier();
    }

}