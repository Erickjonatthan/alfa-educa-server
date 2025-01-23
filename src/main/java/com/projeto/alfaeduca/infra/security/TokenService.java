package com.projeto.alfaeduca.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.alfaeduca.usuario.UserAccount;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public DadosTokenJWT gerarToken(UserAccount usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var dataCriacao = Instant.now().minusSeconds(3 * 3600);
            var token = JWT.create()
                    .withIssuer("API Pgp")
                    .withSubject(usuario.getLogin())
                    .withIssuedAt(dataCriacao)
                    .sign(algoritmo);
            return new DadosTokenJWT(token, usuario.getId(), dataCriacao);
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Pgp")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }
}