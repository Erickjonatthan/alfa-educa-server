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
public class AdminUserManagementTest {

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

    private UserAccount adminUser;
    private UserAccount regularUser;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Criar usuário administrador
        var adminData = new UserRegistrationData(
            "Admin User",
            "admin@test.com", // Este email está na lista de admins em application-test.properties
            "adminpass123"
        );
        adminUser = new UserAccount(adminData, passwordEncoder, List.of("admin@test.com"));
        userRepository.save(adminUser);
        
        // Criar usuário comum
        var userData = new UserRegistrationData(
            "Regular User",
            "regular@test.com",
            "userpass123"
        );
        regularUser = new UserAccount(userData, passwordEncoder, List.of());
        userRepository.save(regularUser);
        
        // Gerar token de admin
        var dadosToken = tokenService.gerarToken(adminUser);
        adminToken = dadosToken.token();
    }

    @Test
    void adminDeveCriarNovoUsuario() throws Exception {
        var newUserData = new UserRegistrationData(
            "New User",
            "new@test.com",
            "newpass123"
        );
        
        mockMvc.perform(post("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserData))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("New User"))
                .andExpect(jsonPath("$.email").value("new@test.com"));

        assertTrue(userRepository.existsByLogin("new@test.com"));
    }

    @Test
    void adminDeveEditarQualquerUsuario() throws Exception {
        var updateData = new UserUpdateData(
            regularUser.getId(), 
            "Updated User", 
            "updated@test.com", 
            "newpassword123", 
            null
        );
        
        mockMvc.perform(put("/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));

        var updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertEquals("Updated User", updatedUser.getNome());
        assertEquals("updated@test.com", updatedUser.getLogin());
    }

    @Test
    void adminDeveDeletarQualquerUsuario() throws Exception {
        mockMvc.perform(delete("/cadastro/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(regularUser.getId()));
    }

    @Test
    void adminDeveMudarRoleDeQualquerUsuario() throws Exception {
        mockMvc.perform(post("/login/mudar-role/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdmin").value(true));

        var updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertTrue(updatedUser.isAdmin());
    }

    @Test
    void adminDeveListarTodosUsuarios() throws Exception {
        mockMvc.perform(get("/cadastro")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").exists())
                .andExpect(jsonPath("$").isArray());
    }
}
