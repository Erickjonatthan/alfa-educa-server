package com.projeto.alfaeduca.domain.resposta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AnswerRegistrationData(
    @NotBlank
    String resposta,

    @NotNull
    UUID atividadeId,

    @NotNull
    UUID usuarioId
) {
}