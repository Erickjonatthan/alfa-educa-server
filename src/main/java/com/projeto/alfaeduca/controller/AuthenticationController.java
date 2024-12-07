
package com.projeto.alfaeduca.controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.alfaeduca.infra.security.DadosTokenJWT;
import com.projeto.alfaeduca.infra.security.SecurityUtils;
import com.projeto.alfaeduca.infra.security.TokenService;
import com.projeto.alfaeduca.usuario.UserAccount;
import com.projeto.alfaeduca.usuario.UserDetailsData;
import com.projeto.alfaeduca.usuario.UserRepository;
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
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid AuthenticationData dados) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());

        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((UserAccount) authentication.getPrincipal());

        var contaId = ((UserAccount) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, contaId));

    }

    @PostMapping("/recuperar-senha/{id}")
    public ResponseEntity<UserDetailsData> recuperarSenha(@PathVariable Long id) {
        
        if (!SecurityUtils.isUserAccessingOwnResource(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<UserAccount> user = repository.findById(id);

        if (user.isPresent()) {
            UserAccount rawUser = user.get();
            // Gere uma nova senha aleatória
            String novaSenha = generateRandomPassword();

            emailService.sendResetPasswordEmail(rawUser, novaSenha);
            rawUser.setSenha(novaSenha, passwordEncoder);
            repository.save(rawUser);

            return ResponseEntity.ok(new UserDetailsData(rawUser));
        }

        return ResponseEntity.notFound().build();
    }

    private String generateRandomPassword() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }
}
