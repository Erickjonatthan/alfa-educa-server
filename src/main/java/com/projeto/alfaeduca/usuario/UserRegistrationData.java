package com.projeto.alfaeduca.usuario;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationData(

    @NotBlank
    String nome,

    @NotBlank
    @Email
    String email,

    @NotBlank
    String senha,
    
    @NotBlank
    String apelido,
    
    String imgUrl    
       ) {

    
}