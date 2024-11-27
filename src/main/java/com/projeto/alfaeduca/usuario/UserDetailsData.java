package com.projeto.alfaeduca.usuario;

public record UserDetailsData(Long id, String nome, String email, String apelido, String imgUrl) {
    public UserDetailsData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getApelido(), user.getImgUrl());
    }
}
