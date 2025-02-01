package com.projeto.alfaeduca.domain.atividade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRegistrationData(
    @NotBlank
    String titulo,

    @NotBlank
    String descricao,

    @NotNull
    int nivel,

    @NotNull
    int pontos,

    @NotBlank
    String respostaCorreta
) {
}