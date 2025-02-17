package com.projeto.alfaeduca.domain.conquista;

import jakarta.validation.constraints.NotBlank;

public record AchievementRegistrationData(

    @NotBlank
    String titulo,

    @NotBlank
    String descricao,

    byte[] imgConquista
) {
    
}
