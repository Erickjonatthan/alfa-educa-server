package com.projeto.alfaeduca.domain.conquista;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.projeto.alfaeduca.domain.usuario.UserAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "conquistas")
@Entity(name = "Conquista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Achievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    private byte[] imgConquista;

    @ManyToMany(mappedBy = "conquistas", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserAccount> usuarios = new ArrayList<>();

    @Column(nullable = true)
    private Integer nivelRequerido;

    @Column(nullable = true)
    private Integer pontosRequeridos;

    @Column(nullable = true)
    private Integer atividadesRequeridas;

    @Column(nullable = true)
    private Boolean primeiraRespostaCorreta;

    @Column(nullable = true)
    private Integer diasConsecutivosRequeridos;

    public Achievement(AchievementRegistrationData achievementRegistrationData) {
        this.titulo = achievementRegistrationData.titulo();
        this.descricao = achievementRegistrationData.descricao();
        this.imgConquista = achievementRegistrationData.imgConquista();
        this.nivelRequerido = achievementRegistrationData.nivelRequerido();
        this.pontosRequeridos = achievementRegistrationData.pontosRequeridos();
        this.atividadesRequeridas = achievementRegistrationData.atividadesRequeridas();
        this.primeiraRespostaCorreta = achievementRegistrationData.primeiraRespostaCorreta();
        this.diasConsecutivosRequeridos = achievementRegistrationData.diasConsecutivosRequeridos();
    }

    // Método para verificar se a conquista pode ser desbloqueada por um usuário
    public boolean podeSerDesbloqueadaPor(UserAccount user) {
        if (this.nivelRequerido != null && user.getNivel() >= this.nivelRequerido) {
            return true;
        }
        if (this.pontosRequeridos != null && user.getPontos() >= this.pontosRequeridos) {
            return true;
        }
        if (this.atividadesRequeridas != null && user.getAtividadesConcluidas() >= this.atividadesRequeridas) {
            return true;
        }
        if (this.primeiraRespostaCorreta != null && this.primeiraRespostaCorreta && user.isPrimeiraRespostaCorreta()) {
            return true;
        }
        if (this.diasConsecutivosRequeridos != null && user.getDiasConsecutivos() >= this.diasConsecutivosRequeridos) {
            return true;
        }
        return false;
    }

    public String calcularProgresso(UserAccount user) {
        if (this.nivelRequerido != null && user.getNivel() < this.nivelRequerido) {
            return "Faltam " + (this.nivelRequerido - user.getNivel()) + " níveis para desbloquear.";
        }
        if (this.pontosRequeridos != null && user.getPontos() < this.pontosRequeridos) {
            return "Faltam " + (this.pontosRequeridos - user.getPontos()) + " pontos para desbloquear.";
        }
        if (this.atividadesRequeridas != null && user.getAtividadesConcluidas() < this.atividadesRequeridas) {
            return "Faltam " + (this.atividadesRequeridas - user.getAtividadesConcluidas()) + " atividades para desbloquear.";
        }
        if (this.primeiraRespostaCorreta != null && this.primeiraRespostaCorreta && !user.isPrimeiraRespostaCorreta()) {
            return "Você precisa acertar uma resposta na primeira tentativa.";
        }
        if (this.diasConsecutivosRequeridos != null && user.getDiasConsecutivos() < this.diasConsecutivosRequeridos) {
            return "Faltam " + (this.diasConsecutivosRequeridos - user.getDiasConsecutivos()) + " dias consecutivos de login para desbloquear.";
        }
        return "Conquista desbloqueada ou sem progresso necessário.";
    }
}