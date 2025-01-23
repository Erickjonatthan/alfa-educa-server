package com.projeto.alfaeduca.infra.security;

import java.time.Instant;
import java.util.UUID;

public record DadosTokenJWT(String token, UUID contaId, Instant dataCriacao) {

}