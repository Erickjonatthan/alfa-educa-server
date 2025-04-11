package com.projeto.alfaeduca.domain.conquista.DTO;

public record AchievementProgressDTO(
        AchievementDetailsDTO conquista,
        String progresso
) {}