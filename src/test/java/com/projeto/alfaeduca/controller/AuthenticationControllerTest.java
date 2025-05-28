package com.projeto.alfaeduca.controller;

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.domain.usuario.authentication.AuthenticationData;
import com.projeto.alfaeduca.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserAccount testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        var userData = new UserRegistrationData(
            "Teste User",
            "teste@email.com",
            "senha123"
        );
        testUser = new UserAccount(userData, passwordEncoder, List.of());
        userRepository.save(testUser);
    }

    @Test
    void deveAutenticarUsuarioComSucesso() throws Exception {
        var authData = new AuthenticationData("teste@email.com", "senha123");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("teste@email.com"))
                .andExpect(jsonPath("$.nome").value("Teste User"))
                .andExpect(jsonPath("$.dadosToken").exists())
                .andExpect(jsonPath("$.isAdmin").value(false));
    }

    @Test
    void deveFalharAutenticacaoComEmailInvalido() throws Exception {
        var authData = new AuthenticationData("emailinexistente@email.com", "senha123");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveFalharAutenticacaoComSenhaInvalida() throws Exception {
        var authData = new AuthenticationData("teste@email.com", "senhaerrada");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveFalharAutenticacaoComEmailEmBranco() throws Exception {
        var authData = new AuthenticationData("", "senha123");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveFalharAutenticacaoComSenhaEmBranco() throws Exception {
        var authData = new AuthenticationData("teste@email.com", "");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authData)))
                .andExpect(status().isUnauthorized());
    }
}
