package com.projeto.alfaeduca.domain.atividade;

import java.util.List;
import java.util.UUID;

import com.projeto.alfaeduca.domain.resposta.Answer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "atividades")
@Entity(name = "Atividade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String subtitulo;

    @Column(nullable = false)
    private int nivel;

    @Column(nullable = false)
    private int pontos;

    @Column(nullable = false)
    private String respostaCorreta;

    @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> respostas;

    public boolean verificarResposta(String respostaUsuario) {
        return this.respostaCorreta.equals(respostaUsuario);
    }

    public Task(TaskRegistrationData taskRegistrationData) {
        this.titulo = taskRegistrationData.titulo();
        this.descricao = taskRegistrationData.descricao();
        this.nivel = taskRegistrationData.nivel();
        this.pontos = taskRegistrationData.pontos();
        this.respostaCorreta = taskRegistrationData.respostaCorreta();
    }
}