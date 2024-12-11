package com.projeto.alfaeduca.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configuração CORS modernizada
            .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                CorsConfiguration corsConfig = new CorsConfiguration().applyPermitDefaultValues();
                corsConfig.addAllowedMethod(HttpMethod.GET);
                corsConfig.addAllowedMethod(HttpMethod.POST);
                corsConfig.addAllowedMethod(HttpMethod.PUT);
                corsConfig.addAllowedMethod(HttpMethod.DELETE);
                corsConfig.addAllowedMethod(HttpMethod.PATCH);
                return corsConfig;
            }))
            // Desabilitar CSRF
            .csrf(csrf -> csrf.disable())
            // Gerenciamento de sessão
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configuração de autorizações
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.POST, "/cadastro").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/login").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/login/recuperar-senha/**").permitAll();
                auth.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll();
                auth.anyRequest().authenticated();
            })
            // Adicionar filtro personalizado
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}