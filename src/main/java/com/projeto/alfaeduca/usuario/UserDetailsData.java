package com.projeto.alfaeduca.usuario;

import java.util.UUID;

public record UserDetailsData(UUID id, String nome, String email, byte[] imgPerfil) {
    public UserDetailsData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getImgPerfil());
    }
}
