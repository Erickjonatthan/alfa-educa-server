package com.projeto.alfaeduca.usuario;



import jakarta.validation.constraints.NotNull;



public record UserUpdateData(@NotNull Long id, String nome, String email, String senha, byte[] imgPerfil) {
    public UserUpdateData(UserAccount user) {
        this(user.getId(), user.getNome(), user.getLogin(), user.getSenha(), user.getImgPerfil());
    }
    
}
