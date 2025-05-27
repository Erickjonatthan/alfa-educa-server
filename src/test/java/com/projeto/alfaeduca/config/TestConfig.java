package com.projeto.alfaeduca.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.projeto.alfaeduca.domain.usuario.email.EmailService;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }

    @Bean
    @Primary
    public OCR ocr() {
        return Mockito.mock(OCR.class);
    }
}
