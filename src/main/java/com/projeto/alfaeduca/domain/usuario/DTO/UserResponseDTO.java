package com.projeto.alfaeduca.domain.usuario.DTO;

import com.projeto.alfaeduca.domain.usuario.UserAccount;

public record UserResponseDTO(
    String nome,
    String email,
    boolean isAdmin
) {
    public UserResponseDTO(UserAccount usuario) {
        this(
            usuario.getNome(),
            usuario.getLogin(),
            usuario.isAdmin()
        );
    }
}
