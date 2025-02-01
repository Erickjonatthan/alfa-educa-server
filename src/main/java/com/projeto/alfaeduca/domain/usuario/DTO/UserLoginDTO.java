package com.projeto.alfaeduca.domain.usuario.DTO;

import com.projeto.alfaeduca.infra.security.DadosTokenJWT;

public record UserLoginDTO(String nome, String email, DadosTokenJWT dadosToken, boolean isAdmin) {
}