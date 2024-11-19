package com.projeto.gfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.gfinder.preferencia.Preference;
import com.projeto.gfinder.preferencia.PreferenceDetailsData;
import com.projeto.gfinder.preferencia.PreferenceRepository;
import com.projeto.gfinder.usuario.UserAccount;
import com.projeto.gfinder.usuario.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/preferencias")
public class PreferenceController {
    
    @Autowired
    private PreferenceRepository repository;

    @Autowired
    private UserRepository userAccountRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<PreferenceDetailsData> adicionar(@RequestBody Preference preference, UriComponentsBuilder uriBuilder) {
        // Recupera o UserAccount do banco de dados
        UserAccount usuario = userAccountRepository.findById(preference.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Associa o UserAccount à preferência
        preference.setUsuario(usuario);

        var uri = uriBuilder.path("/preferencias/{id}").buildAndExpand(preference.getId()).toUri();

        // Salva a preferência
        Preference savedPreference = repository.save(preference);
        
        return ResponseEntity.created(uri).body(new PreferenceDetailsData(savedPreference));
    }
}