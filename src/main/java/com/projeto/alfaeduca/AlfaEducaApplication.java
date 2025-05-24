package com.projeto.alfaeduca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.projeto.alfaeduca.domain.task.Task;
import com.projeto.alfaeduca.domain.task.TaskRepository;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRepository;

import java.util.List;

@SpringBootApplication
public class AlfaEducaApplication {

    private static final Logger log = LoggerFactory.getLogger(AlfaEducaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AlfaEducaApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepository, TaskRepository taskRepository) {
        return (args) -> {
            log.info("Starting task backfill process...");

            List<UserAccount> users = userRepository.findAll();

            for (UserAccount user : users) {
                String expectedTitle = "Caligrafia inicial para " + user.getNome();
                // Use the new repository method
                java.util.Optional<Task> existingTask = taskRepository.findByTituloAndTipo(expectedTitle, "CALIGRAFIA");

                if (existingTask.isEmpty()) {
                    Task newTask = new Task();
                    newTask.setTipo("CALIGRAFIA");
                    newTask.setDescricao(user.getNome());
                    newTask.setTitulo(expectedTitle);
                    newTask.setSubtitulo("Exercício de caligrafia");
                    newTask.setNivel(1);
                    newTask.setPontos(10);
                    newTask.setRespostaCorreta(user.getNome()); // Placeholder for calligraphy
                    taskRepository.save(newTask);
                    log.info("Created calligraphy task titled '{}' for user: {}", expectedTitle, user.getNome());
                } else {
                    log.info("Calligraphy task titled '{}' already exists for user: {}", expectedTitle, user.getNome());
                }
            }
            log.info("Task backfill process completed.");
        };
    }
}