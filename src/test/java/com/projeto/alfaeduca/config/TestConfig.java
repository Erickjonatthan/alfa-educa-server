package com.projeto.alfaeduca.config;

import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.projeto.alfaeduca.domain.usuario.email.EmailService;
import com.projeto.alfaeduca.domain.usuario.email.EmailVerifier;

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
    public ChatClient chatClient() {
        return Mockito.mock(ChatClient.class);
    }

    @Bean
    @Primary
    public EmailVerifier emailVerifier() {
        EmailVerifier mockVerifier = Mockito.mock(EmailVerifier.class);
        Mockito.when(mockVerifier.verificaEmail(Mockito.anyString())).thenReturn(true);
        return mockVerifier;
    }
}
