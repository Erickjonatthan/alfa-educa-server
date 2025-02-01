package com.projeto.alfaeduca.domain.resposta.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.alfaeduca.domain.resposta.Answer;

public record AnswerDetailsDTO(UUID id, String resposta, UUID atividadeId, UUID usuarioId, boolean finalizada, LocalDateTime dataResposta) {
    public AnswerDetailsDTO(Answer answer) {
        this(answer.getId(), answer.getResposta(), answer.getAtividade().getId(), answer.getUsuario().getId(), answer.getFinalizada(), answer.getDataResposta());
    }  
}