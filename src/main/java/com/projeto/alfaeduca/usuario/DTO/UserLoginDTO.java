package com.projeto.alfaeduca.usuario.DTO;

import com.projeto.alfaeduca.infra.security.DadosTokenJWT;

public record UserLoginDTO(String nome, String email, DadosTokenJWT dadosToken, boolean isAdmin) {
}