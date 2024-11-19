package com.projeto.gfinder.usuario;

public record UserDetailsData(Long id, String nome, String email, String senha, String apelido, String imgUrl) {
    public UserDetailsData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getSenha(), user.getApelido(), user.getImgUrl());
    }
}
