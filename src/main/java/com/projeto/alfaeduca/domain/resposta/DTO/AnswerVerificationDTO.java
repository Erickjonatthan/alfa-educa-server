package com.projeto.alfaeduca.domain.resposta.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.alfaeduca.domain.resposta.Answer;

public record AnswerVerificationDTO(UUID atividadeId, UUID usuarioId, boolean isFinalizada, LocalDateTime dataResposta) {
    public AnswerVerificationDTO(Answer resposta) {
        this(resposta.getAtividade().getId(), resposta.getUsuario().getId(), resposta.getFinalizada(), resposta.getDataResposta());
    }
}