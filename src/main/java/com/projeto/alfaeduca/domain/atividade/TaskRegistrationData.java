package com.projeto.alfaeduca.domain.atividade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRegistrationData(
    @NotBlank
    String titulo,

    @NotBlank
    String subtitulo,

    @NotBlank
    String descricao,

    @NotNull
    int nivel,

    @NotNull
    int pontos,

    @NotNull
    String tipo,

    @NotBlank
    String respostaCorreta
) {
}