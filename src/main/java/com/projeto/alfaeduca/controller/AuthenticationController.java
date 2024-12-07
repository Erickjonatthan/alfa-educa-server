
package com.projeto.alfaeduca.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.alfaeduca.infra.security.DadosTokenJWT;
import com.projeto.alfaeduca.infra.security.TokenService;
import com.projeto.alfaeduca.usuario.UserAccount;
import com.projeto.alfaeduca.usuario.UserRepository;
import com.projeto.alfaeduca.usuario.UserUpdateData;
import com.projeto.alfaeduca.usuario.authentication.AuthenticationData;
import com.projeto.alfaeduca.usuario.email.EmailService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/login")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private EmailService emailService;


    @PostMapping
    public ResponseEntity efeturarLogin(@RequestBody @Valid AuthenticationData
    dados){

        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((UserAccount) authentication.getPrincipal());
        var contaId =  ((UserAccount) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, contaId));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity recuperarSenha(@RequestBody @Valid UserUpdateData dados) {
        
        UserAccount user = repository.getReferenceById(dados.id());
        
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        // Gere uma nova senha aleatória
        String novaSenha = generateRandomPassword();

        emailService.sendResetPasswordEmail(user, novaSenha);
        user.setSenha(novaSenha, passwordEncoder);
        repository.save(user);
        
        
        return ResponseEntity.ok().build();
    }

    private String generateRandomPassword() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }
}
    

