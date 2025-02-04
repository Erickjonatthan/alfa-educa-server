package com.projeto.alfaeduca.domain.atividade.DTO;

import java.util.UUID;
import com.projeto.alfaeduca.domain.atividade.Task;

public record TaskDetailsDTO(UUID id, String titulo, String subtitulo,  String descricao, int nivel, int pontos) {
    
    public TaskDetailsDTO(Task task) {
        this(task.getId(), task.getTitulo(), task.getSubtitulo(), task.getDescricao(), task.getNivel(), task.getPontos());
    }
}