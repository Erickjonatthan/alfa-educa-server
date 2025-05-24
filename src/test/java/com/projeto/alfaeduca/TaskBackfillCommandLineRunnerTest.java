package com.projeto.alfaeduca;

import com.projeto.alfaeduca.domain.task.Task;
import com.projeto.alfaeduca.domain.task.TaskRepository;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskBackfillCommandLineRunnerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    private CommandLineRunner commandLineRunner;

    private UserAccount testUser;

    @BeforeEach
    void setUp() {
        // Instantiate AlfaEducaApplication to get the CommandLineRunner bean
        // This directly calls the 'demo' method which returns the CommandLineRunner instance
        AlfaEducaApplication alfaEducaApplication = new AlfaEducaApplication();
        commandLineRunner = alfaEducaApplication.demo(userRepository, taskRepository);

        testUser = new UserAccount();
        // Required fields for UserAccount to avoid NullPointerExceptions if .getNome() is called
        ReflectionTestUtils.setField(testUser, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(testUser, "nome", "Test User");
        ReflectionTestUtils.setField(testUser, "login", "test@example.com");
        ReflectionTestUtils.setField(testUser, "senha", "password");

    }

    @Test
    @DisplayName("CommandLineRunner should create task if user does not have it")
    void run_whenUserLacksTask_shouldCreateTask() throws Exception {
        // Arrange
        String expectedTitle = "Caligrafia inicial para " + testUser.getNome();
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));
        when(taskRepository.findByTituloAndTipo(expectedTitle, "CALIGRAFIA")).thenReturn(Optional.empty());

        // Act
        commandLineRunner.run(new String[]{});

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertThat(savedTask.getTipo()).isEqualTo("CALIGRAFIA");
        assertThat(savedTask.getDescricao()).isEqualTo(testUser.getNome());
        assertThat(savedTask.getTitulo()).isEqualTo(expectedTitle);
        assertThat(savedTask.getSubtitulo()).isEqualTo("Exercício de caligrafia");
        assertThat(savedTask.getNivel()).isEqualTo(1);
        assertThat(savedTask.getPontos()).isEqualTo(10);
        assertThat(savedTask.getRespostaCorreta()).isEqualTo(testUser.getNome());
    }

    @Test
    @DisplayName("CommandLineRunner should not create task if user already has it")
    void run_whenUserHasTask_shouldNotCreateTask() throws Exception {
        // Arrange
        String expectedTitle = "Caligrafia inicial para " + testUser.getNome();
        Task existingTask = new Task(); // Dummy task
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));
        when(taskRepository.findByTituloAndTipo(expectedTitle, "CALIGRAFIA")).thenReturn(Optional.of(existingTask));

        // Act
        commandLineRunner.run(new String[]{});

        // Assert
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    @DisplayName("CommandLineRunner should handle multiple users correctly")
    void run_withMultipleUsers_shouldProcessAll() throws Exception {
        // Arrange
        UserAccount user1 = new UserAccount();
        ReflectionTestUtils.setField(user1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(user1, "nome", "User One");
        ReflectionTestUtils.setField(user1, "login", "user1@example.com");
        ReflectionTestUtils.setField(user1, "senha", "pw1");
        String title1 = "Caligrafia inicial para " + user1.getNome();

        UserAccount user2 = new UserAccount();
        ReflectionTestUtils.setField(user2, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(user2, "nome", "User Two");
        ReflectionTestUtils.setField(user2, "login", "user2@example.com");
        ReflectionTestUtils.setField(user2, "senha", "pw2");
        String title2 = "Caligrafia inicial para " + user2.getNome();
        
        Task existingTaskForUser2 = new Task(); // User Two already has the task

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(taskRepository.findByTituloAndTipo(title1, "CALIGRAFIA")).thenReturn(Optional.empty());
        when(taskRepository.findByTituloAndTipo(title2, "CALIGRAFIA")).thenReturn(Optional.of(existingTaskForUser2));

        // Act
        commandLineRunner.run(new String[]{});

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture()); // Only one task should be saved (for user1)
        Task savedTaskForUser1 = taskCaptor.getValue();

        assertThat(savedTaskForUser1.getTitulo()).isEqualTo(title1);
        assertThat(savedTaskForUser1.getDescricao()).isEqualTo(user1.getNome());

        verify(taskRepository).findByTituloAndTipo(title1, "CALIGRAFIA");
        verify(taskRepository).findByTituloAndTipo(title2, "CALIGRAFIA");
    }

    @Test
    @DisplayName("CommandLineRunner should not fail with empty user list")
    void run_whenNoUsers_shouldCompleteWithoutError() throws Exception {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        commandLineRunner.run(new String[]{});

        // Assert
        verify(taskRepository, never()).findByTituloAndTipo(anyString(), anyString());
        verify(taskRepository, never()).save(any(Task.class));
    }
}

// Helper for setting private fields in UserAccount if needed, or use public setters if available
class ReflectionTestUtils {
    public static void setField(Object targetObject, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(targetObject, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
