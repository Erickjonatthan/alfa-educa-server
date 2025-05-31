package com.projeto.alfaeduca.controller;

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.domain.usuario.UserUpdateData;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserAccountControllerTest {

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
    private String userToken;

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
        
        var dadosToken = tokenService.gerarToken(testUser);
        userToken = dadosToken.token();
    }

    @Test
    void deveCriarNovoUsuarioComSucesso() throws Exception {
        var newUserData = new UserRegistrationData(
            "Novo Usuario",
            "novo@email.com",
            "senha456"
        );
        
        mockMvc.perform(post("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Usuario"))
                .andExpect(jsonPath("$.email").value("novo@email.com"));

        var savedUser = userRepository.findByLogin("novo@email.com");
        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches("senha456", savedUser.getSenha()));
    }

    @Test
    void deveAtualizarUsuarioExistente() throws Exception {
        var updateData = new UserUpdateData(
            testUser.getId(), 
            "Usuario Atualizado", 
            null, 
            "novaSenha123", 
            null
        );
        
        mockMvc.perform(put("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Usuario Atualizado"));

        var updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("Usuario Atualizado", updatedUser.getNome());
        assertTrue(passwordEncoder.matches("novaSenha123", updatedUser.getSenha()));
    }

    @Test
    void deveDeletarUsuarioExistente() throws Exception {
        mockMvc.perform(delete("/cadastro/" + testUser.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(testUser.getId()));
    }

    @Test
    void naoDeveAtualizarUsuarioSemAutenticacao() throws Exception {
        var updateData = new UserUpdateData(
            testUser.getId(), 
            "Usuario Atualizado", 
            null,
            "novaSenha123", 
            null
        );
        
        mockMvc.perform(put("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveDeletarUsuarioSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/cadastro/" + testUser.getId()))
                .andExpect(status().isUnauthorized());

        assertTrue(userRepository.existsById(testUser.getId()));
    }

    @Test
    void naoDeveCriarUsuarioComEmailDuplicado() throws Exception {
        var duplicateUserData = new UserRegistrationData(
            "Duplicate User",
            "teste@email.com", // mesmo email do testUser
            "senha789"
        );
        
        mockMvc.perform(post("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUserData)))
                .andExpect(status().isBadRequest());
    }
}
