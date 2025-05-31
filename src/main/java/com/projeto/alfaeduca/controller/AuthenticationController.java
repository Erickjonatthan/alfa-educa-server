package com.projeto.alfaeduca.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserDataForgotPassword;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.domain.usuario.DTO.UserDetailsDTO;
import com.projeto.alfaeduca.domain.usuario.DTO.UserLoginDTO;
import com.projeto.alfaeduca.domain.usuario.authentication.AuthenticationData;
import com.projeto.alfaeduca.domain.usuario.email.EmailService;
import com.projeto.alfaeduca.infra.security.SecurityUtils;
import com.projeto.alfaeduca.infra.security.TokenService;

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
    public ResponseEntity<UserLoginDTO> efetuarLogin(@RequestBody @Valid AuthenticationData dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);
        var userAccount = (UserAccount) authentication.getPrincipal();
        var token = tokenService.gerarToken(userAccount);
        var isAdmin = userAccount.isAdmin();

        // Registrar login e atualizar dias consecutivos
        LocalDate ultimoLogin = userAccount.getUltimoLogin();
        LocalDate hoje = LocalDate.now();

        if (ultimoLogin != null && ultimoLogin.plusDays(1).isEqual(hoje)) {
            userAccount.incrementarDiasConsecutivos();
        } else if (ultimoLogin == null || !ultimoLogin.isEqual(hoje)) {
            userAccount.setDiasConsecutivos(1); // Reinicia a contagem
        }

        userAccount.setUltimoLogin(hoje);
        repository.save(userAccount);

        var userLoginData = new UserLoginDTO(userAccount.getNome(), userAccount.getLogin(), token, isAdmin);

        return ResponseEntity.ok(userLoginData);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<UserDetailsDTO> recuperarSenha(@RequestBody UserDataForgotPassword data) {

        UserAccount user = repository.findByLogin(data.email());
        if (user != null) {
            // Gere uma nova senha aleatória
            String novaSenha = generateRandomPassword();

            emailService.sendResetPasswordEmail(user, novaSenha);
            user.setSenha(novaSenha, passwordEncoder);
            repository.save(user);

            return ResponseEntity.ok(new UserDetailsDTO(user));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/mudar-role/{id}")
    public ResponseEntity<UserDetailsDTO> mudarRole(@PathVariable UUID id) {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado == null || !usuarioAutenticado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            var usuario = repository.getReferenceById(id);
            var roles = new ArrayList<>(usuario.getRoles());
            if (roles.contains("ROLE_ADMIN")) {
                // Se o usuário é admin, remove a role de admin e mantém apenas a de usuário
                roles.remove("ROLE_ADMIN");
            } else {
                // Se o usuário é comum, adiciona a role de admin
                roles.add("ROLE_ADMIN");
            }
            usuario.setRoles(roles);
            repository.save(usuario);

            return ResponseEntity.ok(new UserDetailsDTO(usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String generateRandomPassword() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }
}
