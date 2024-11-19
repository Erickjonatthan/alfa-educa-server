package com.projeto.gfinder.usuario;



import jakarta.validation.constraints.NotNull;



public record UserUpdateData(@NotNull Long id, String nome, String email, String senha, String apelido, String imgUrl) {
    public UserUpdateData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getSenha(), user.getApelido(), user.getImgUrl());
    }
    
}
