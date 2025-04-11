package com.projeto.alfaeduca.domain.conquista.DTO;

import java.util.UUID;

import com.projeto.alfaeduca.domain.conquista.Achievement;

public record AchievementDetailsDTO(
        UUID id,
        String titulo,
        String descricao,
        byte[] imgConquista,
        Integer nivelRequerido,
        Integer pontosRequeridos,
        Integer atividadesRequeridas,
        Boolean primeiraRespostaCorreta,
        Integer diasConsecutivosRequeridos) {

    public AchievementDetailsDTO(Achievement achievement) {
        this(
            achievement.getId(),
            achievement.getTitulo(),
            achievement.getDescricao(),
            achievement.getImgConquista(),
            achievement.getNivelRequerido(),
            achievement.getPontosRequeridos(),
            achievement.getAtividadesRequeridas(),
            achievement.getPrimeiraRespostaCorreta(),
            achievement.getDiasConsecutivosRequeridos()
        );
    }
}