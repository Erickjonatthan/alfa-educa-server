package com.projeto.alfaeduca.usuario.DTO;

import java.util.UUID;

import com.projeto.alfaeduca.usuario.UserAccount;

public record UserDetailsDTO(UUID id, String nome, String email, byte[] imgPerfil) {
    public UserDetailsDTO(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getImgPerfil());
    }
}
