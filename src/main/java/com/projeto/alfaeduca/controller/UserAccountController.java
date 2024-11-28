package com.projeto.alfaeduca.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.projeto.alfaeduca.usuario.UserAccount;
import com.projeto.alfaeduca.usuario.UserDetailsData;
import com.projeto.alfaeduca.usuario.UserRegistrationData;
import com.projeto.alfaeduca.usuario.UserRepository;
import com.projeto.alfaeduca.usuario.UserUpdateData;
import com.projeto.alfaeduca.usuario.email.EmailService;

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

    @PostMapping
    @Transactional
    public ResponseEntity<UserDetailsData> cadastrar(@RequestBody @Valid UserRegistrationData dados, UriComponentsBuilder uriBuilder) {
        
        if(repository.existsByLogin(dados.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var usuario = new UserAccount(dados,passwordEncoder);
        repository.save(usuario);
        var uri = uriBuilder.path("/cadastro/{id}").buildAndExpand(usuario.getId()).toUri();


        emailService.sendWelcomeEmail(usuario);

        return ResponseEntity.created(uri).body(new UserDetailsData(usuario));
    }
    
    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid UserUpdateData dados ){
        
        var usuario = repository.getReferenceById(dados.id());
        usuario.atualizarInformacoes(dados, passwordEncoder);
        
        return ResponseEntity.ok(new UserDetailsData(usuario));

    }
    
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity remover(@PathVariable Long id ) {

        var usuario = repository.getReferenceById(id);
        repository.delete(usuario);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsData> detalhar(@PathVariable Long id  ){
        
         var usuario = repository.getReferenceById(id);
         return ResponseEntity.ok(new UserDetailsData(usuario));
    }
}