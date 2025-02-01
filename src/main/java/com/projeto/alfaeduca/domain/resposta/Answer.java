package com.projeto.alfaeduca.domain.resposta;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.alfaeduca.domain.atividade.Task;
import com.projeto.alfaeduca.domain.atividade.TaskRepository;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "respostas")
@Entity(name = "Resposta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String resposta;

    @ManyToOne
    @JoinColumn(name = "atividade_id", nullable = false)
    private Task atividade;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserAccount usuario;

    @Column(nullable = false, name = "data_resposta")
    private LocalDateTime dataResposta;

    private Boolean finalizada = false;

    public Answer(AnswerRegistrationData answerRegistrationData, TaskRepository taskRepository, UserRepository userRepository) {
        this.resposta = answerRegistrationData.resposta();

        var idAtividade = answerRegistrationData.atividadeId();
        
        this.atividade = taskRepository.findById(idAtividade)
            .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        var idUsuario = answerRegistrationData.usuarioId();
        
        this.usuario = userRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        this.dataResposta = LocalDateTime.now();

    }

    public boolean isRespostaCorreta() {
        return this.atividade.verificarResposta(this.resposta);
    }
}