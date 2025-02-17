package com.projeto.alfaeduca.domain.atividade.DTO;


import java.util.UUID;
import com.projeto.alfaeduca.domain.atividade.Task;
import com.projeto.alfaeduca.infra.security.SecurityUtils;

public record TaskDetailsDTO(UUID id, String titulo, String subtitulo, String descricao, int nivel, int pontos, String respostaCorreta, String tipo) {

    public TaskDetailsDTO(Task task) {
        this(task.getId(), task.getTitulo(), task.getSubtitulo(), task.getDescricao(), task.getNivel(), task.getPontos(), getRespostaCorreta(task), task.getTipo());
    }

    private static String getRespostaCorreta(Task task) {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado != null && (usuarioAutenticado.isAdmin() || task.getQuantidadeRespostasEnviadas(usuarioAutenticado) >= 3)) {
            return task.getRespostaCorreta();
        } else {
            return null;
        }
    }
}