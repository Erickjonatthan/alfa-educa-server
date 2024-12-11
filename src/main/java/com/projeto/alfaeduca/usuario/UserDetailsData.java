package com.projeto.alfaeduca.usuario;

public record UserDetailsData(Long id, String nome, String email, byte[] imgPerfil) {
    public UserDetailsData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getImgPerfil());
    }
}
