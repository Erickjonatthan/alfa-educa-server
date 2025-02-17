package com.projeto.alfaeduca.domain.usuario.DTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.conquista.DTO.AchievementDetailsDTO;

public record UserDetailsDTO(UUID id, String nome, String email, byte[] imgPerfil, int nivel, int pontos, boolean isAdmin, List<AchievementDetailsDTO> conquistas) {
    public UserDetailsDTO(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getImgPerfil(), user.getNivel(), user.getPontos(), user.isAdmin(), 
             user.getConquistas().stream().map(AchievementDetailsDTO::new).collect(Collectors.toList()));
    }
}