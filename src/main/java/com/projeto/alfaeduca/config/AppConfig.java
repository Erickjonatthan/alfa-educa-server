package com.projeto.alfaeduca.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.projeto.alfaeduca.domain.usuario.email.EmailVerifier;

@Configuration
public class AppConfig {

    @Bean
    @Profile("!test")
    public EmailVerifier emailVerifier() {
        return new EmailVerifier();
    }

    @Bean
    @Profile("!test")
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

}