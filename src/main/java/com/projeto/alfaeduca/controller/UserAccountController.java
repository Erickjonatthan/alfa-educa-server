package com.projeto.alfaeduca.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.alfaeduca.infra.security.SecurityUtils;
import com.projeto.alfaeduca.usuario.UserAccount;
import com.projeto.alfaeduca.usuario.UserDetailsData;
import com.projeto.alfaeduca.usuario.UserRegistrationData;
import com.projeto.alfaeduca.usuario.UserRepository;
import com.projeto.alfaeduca.usuario.UserUpdateData;
import com.projeto.alfaeduca.usuario.email.EmailService;
import com.projeto.alfaeduca.usuario.email.EmailVerifier;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cadastro")
public class UserAccountController {
    
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerifier emailVerifier;

    @Value("${admin.emails}")
    private String adminEmails;

    @PostMapping
    @Transactional
    public ResponseEntity<UserDetailsData> cadastrar(@RequestBody @Valid UserRegistrationData dados, UriComponentsBuilder uriBuilder) {
        
        if(repository.existsByLogin(dados.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // Verifica se o email é válido:
        if (!emailVerifier.verificaEmail(dados.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        List<String> adminEmailList = Arrays.asList(adminEmails.split(","));
        var usuario = new UserAccount(dados, passwordEncoder, adminEmailList);
        repository.save(usuario);
        var uri = uriBuilder.path("/cadastro/{id}").buildAndExpand(usuario.getId()).toUri();

        emailService.sendWelcomeEmail(usuario);

        return ResponseEntity.created(uri).body(new UserDetailsData(usuario));
    }
    
    @PutMapping
    @Transactional
    public ResponseEntity<UserDetailsData> atualizar(@RequestBody @Valid UserUpdateData dados) {
        if (!SecurityUtils.isUserAccessingOwnResource(dados.id())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    
        var usuario = repository.getReferenceById(dados.id());
        boolean senhaAlterada = dados.senha() != null && !passwordEncoder.matches(dados.senha(), usuario.getSenha());
    
        usuario.atualizarInformacoes(dados, passwordEncoder);
    
        if (senhaAlterada) {
            emailService.sendPasswordChangeEmail(usuario);
        }
    
        return ResponseEntity.ok(new UserDetailsData(usuario));
    }
   
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> remover(@PathVariable UUID id ) {
        
        if (!SecurityUtils.isUserAccessingOwnResource(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var usuario = repository.getReferenceById(id);

        emailService.sendAccountDeletionEmail(usuario);

        repository.delete(usuario);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsData> detalhar(@PathVariable UUID id  ){
        
        if (!SecurityUtils.isUserAccessingOwnResource(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

         var usuario = repository.getReferenceById(id);
         return ResponseEntity.ok(new UserDetailsData(usuario));
    }

    
}