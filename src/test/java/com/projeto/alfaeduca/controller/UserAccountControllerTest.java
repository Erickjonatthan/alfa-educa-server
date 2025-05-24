package com.projeto.alfaeduca.controller;

import com.projeto.alfaeduca.domain.task.Task;
import com.projeto.alfaeduca.domain.task.TaskRepository;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.domain.usuario.email.EmailService;
import com.projeto.alfaeduca.domain.usuario.email.EmailVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailVerifier emailVerifier;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserAccountController userAccountController;

    private UserRegistrationData userRegistrationData;
    private UserAccount userAccount;
    private UriComponentsBuilder uriBuilder;

    @Value("${admin.emails}")
    private String adminEmails = "admin@example.com"; // Provide a default for testing

    @BeforeEach
    void setUp() {
        userRegistrationData = new UserRegistrationData(
                "Test User",
                "testuser@example.com",
                "password123",
                "1234567890"
        );
        userAccount = new UserAccount(userRegistrationData, passwordEncoder, java.util.Arrays.asList(adminEmails.split(",")));
        // Manually set ID for consistent testing, as it's generated in UserAccount constructor
        UUID fixedId = UUID.randomUUID();
        ReflectionTestUtils.setField(userAccount, "id", fixedId);


        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // Set the adminEmails field in the controller, as @Value won't work in plain unit tests
        ReflectionTestUtils.setField(userAccountController, "adminEmails", adminEmails);
    }

    @Test
    @DisplayName("cadastrar should register user and create initial calligraphy task")
    void cadastrar_whenSuccessfulRegistration_shouldCreateTask() {
        // Arrange
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(emailVerifier.verificaEmail(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAccount.class))).thenReturn(userAccount); // Return the userAccount with ID

        // Act
        ResponseEntity<?> response = userAccountController.cadastrar(userRegistrationData, uriBuilder);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify userRepository.save()
        ArgumentCaptor<UserAccount> userAccountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(userAccountCaptor.capture());
        UserAccount savedUser = userAccountCaptor.getValue();
        assertThat(savedUser.getNome()).isEqualTo("Test User");

        // Verify taskRepository.save() and capture Task
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertThat(savedTask.getTipo()).isEqualTo("CALIGRAFIA");
        assertThat(savedTask.getDescricao()).isEqualTo(userAccount.getNome());
        assertThat(savedTask.getTitulo()).isEqualTo("Caligrafia inicial para " + userAccount.getNome());
        assertThat(savedTask.getSubtitulo()).isEqualTo("Exercício de caligrafia");
        assertThat(savedTask.getNivel()).isEqualTo(1);
        assertThat(savedTask.getPontos()).isEqualTo(10);
        assertThat(savedTask.getRespostaCorreta()).isEqualTo(userAccount.getNome());

        verify(emailService).sendWelcomeEmail(any(UserAccount.class));
    }

    @Test
    @DisplayName("cadastrar should return BAD_REQUEST if user already exists")
    void cadastrar_whenUserExists_shouldReturnBadRequest() {
        // Arrange
        when(userRepository.existsByLogin(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = userAccountController.cadastrar(userRegistrationData, uriBuilder);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("cadastrar should return BAD_REQUEST if email is invalid")
    void cadastrar_whenEmailInvalid_shouldReturnBadRequest() {
        // Arrange
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(emailVerifier.verificaEmail(anyString())).thenReturn(false);

        // Act
        ResponseEntity<?> response = userAccountController.cadastrar(userRegistrationData, uriBuilder);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(taskRepository, never()).save(any(Task.class));
    }
}
