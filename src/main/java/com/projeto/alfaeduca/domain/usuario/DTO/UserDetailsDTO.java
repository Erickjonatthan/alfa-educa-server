package com.projeto.alfaeduca.domain.usuario.DTO;

import java.util.UUID;

import com.projeto.alfaeduca.domain.usuario.UserAccount;

public record UserDetailsDTO(UUID id, String nome, String email, byte[] imgPerfil, int nivel, int pontos, boolean isAdmin) {
    public UserDetailsDTO(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getImgPerfil(), user.getNivel(), user.getPontos(), user.isAdmin());
    }
}
