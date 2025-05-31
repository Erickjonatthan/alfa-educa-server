package com.projeto.alfaeduca.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.alfaeduca.domain.atividade.Task;
import com.projeto.alfaeduca.domain.atividade.TaskRegistrationData;
import com.projeto.alfaeduca.domain.atividade.TaskRepository;
import com.projeto.alfaeduca.domain.atividade.DTO.TaskDetailsDTO;
import com.projeto.alfaeduca.infra.security.SecurityUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/atividade")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<TaskDetailsDTO> cadastrar(@RequestBody @Valid TaskRegistrationData dados,
            UriComponentsBuilder uriBuilder) {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado == null || !usuarioAutenticado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var atividade = new Task(dados);
        repository.save(atividade);

        var uri = uriBuilder.path("/atividade/{id}").buildAndExpand(atividade.getId()).toUri();

        return ResponseEntity.created(uri).body(new TaskDetailsDTO(atividade));
    }

    // Listar todas as atividades
    @GetMapping
    public ResponseEntity<List<TaskDetailsDTO>> listar() {
        var atividades = repository.findAll();
        return ResponseEntity.ok(atividades.stream().map(TaskDetailsDTO::new).collect(Collectors.toList()));
    }

    // Listar atividade pelo id
    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailsDTO> detalhar(@PathVariable UUID id) {
        var atividade = repository.findById(id);
        if (atividade.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new TaskDetailsDTO(atividade.get()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletar(@PathVariable UUID id) {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado == null || !usuarioAutenticado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}