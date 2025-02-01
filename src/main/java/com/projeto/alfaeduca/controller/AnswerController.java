package com.projeto.alfaeduca.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.alfaeduca.domain.atividade.TaskRepository;
import com.projeto.alfaeduca.domain.resposta.Answer;
import com.projeto.alfaeduca.domain.resposta.AnswerRegistrationData;
import com.projeto.alfaeduca.domain.resposta.AnswerRepository;
import com.projeto.alfaeduca.domain.resposta.DTO.AnswerDetailsDTO;
import com.projeto.alfaeduca.domain.resposta.DTO.AnswerVerificationDTO;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/resposta")
public class AnswerController {

    @Autowired
    private AnswerRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<AnswerDetailsDTO> cadastrar(@RequestBody @Valid AnswerRegistrationData dados, UriComponentsBuilder uriBuilder) {
    
        // Obtém o usuário a partir do repositório
        var usuario = userRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    
        // Verifica se já existe uma resposta finalizada para a atividade e usuário
        var respostasExistentes = repository.findByUsuarioAndAtividadeId(usuario, dados.atividadeId());
        boolean existeRespostaFinalizada = respostasExistentes.stream().anyMatch(Answer::getFinalizada);
    
        if (existeRespostaFinalizada) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    
        var resposta = new Answer(dados, taskRepository, userRepository);
        repository.save(resposta);
    
        var uri = uriBuilder.path("/resposta/{id}").buildAndExpand(resposta.getId()).toUri();
    
        return ResponseEntity.created(uri).body(new AnswerDetailsDTO(resposta));
    }

    @GetMapping("/verificar/{respostaId}")
    public ResponseEntity<AnswerVerificationDTO> verificarResposta(@PathVariable UUID respostaId) {
        var resposta = answerRepository.findById(respostaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resposta não encontrada"));
    
        boolean isCorreta = resposta.isRespostaCorreta();
    
        if (isCorreta) {
            var usuario = resposta.getUsuario();
            int pontos = resposta.getAtividade().getPontos();
            usuario.adicionarPontos(pontos);
            userRepository.save(usuario);
            resposta.setFinalizada(true);
            answerRepository.save(resposta); // Salva a entidade resposta após modificar
        }
    
        return ResponseEntity.ok(new AnswerVerificationDTO(resposta));
    }

    @GetMapping("/usuario/respostas")
    public ResponseEntity<List<AnswerDetailsDTO>> listarRespostasUsuario(@AuthenticationPrincipal UserAccount usuario) {
        var respostas = answerRepository.findByUsuario(usuario);
        var respostasDTO = respostas.stream()
                .map(AnswerDetailsDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respostasDTO);
    }

}