package com.projeto.alfaeduca.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.alfaeduca.domain.conquista.AchievementRepository;
import com.projeto.alfaeduca.domain.conquista.DTO.AchievementDetailsDTO;
import com.projeto.alfaeduca.domain.conquista.DTO.AchievementProgressDTO;
import com.projeto.alfaeduca.domain.conquista.Achievement;
import com.projeto.alfaeduca.domain.conquista.AchievementRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.infra.security.SecurityUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conquista")
public class AchievementController {

    @Autowired
    private AchievementRepository repository;

    @Autowired
    private UserRepository userRepository;

    // Cadastrar uma nova conquista
    @PostMapping
    @Transactional
    public ResponseEntity<AchievementDetailsDTO> cadastrar(@RequestBody @Valid AchievementRegistrationData dados,
            UriComponentsBuilder uriBuilder) {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado == null || !usuarioAutenticado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var conquista = new Achievement(dados);
        repository.save(conquista);
        var uri = uriBuilder.path("/conquista/{id}").buildAndExpand(conquista.getId()).toUri();

        return ResponseEntity.created(uri).body(new AchievementDetailsDTO(conquista));
    }

    // Listar todas as conquistas cadastradas
    @GetMapping
    public ResponseEntity<List<AchievementDetailsDTO>> listar() {
        var usuarioAutenticado = SecurityUtils.getAuthenticatedUser();
        if (usuarioAutenticado == null || !usuarioAutenticado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var conquistas = repository.findAll();
        var conquistasDTO = conquistas.stream()
                .map(AchievementDetailsDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conquistasDTO);
    }

    // Verificar conquistas pendentes para um usuário
    @GetMapping("/verificar/{userId}")
    public ResponseEntity<List<AchievementProgressDTO>> verificarConquistasPendentes(@PathVariable UUID userId) {
        if (!SecurityUtils.isUserAccessingOwnResource(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var usuario = userRepository.findByIdWithConquistas(userId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = usuario.get();
        var conquistas = repository.findAll();

        // Filtrar conquistas pendentes e calcular progresso
        var conquistasPendentes = conquistas.stream()
                .filter(conquista -> !user.getConquistas().contains(conquista)) // Apenas as que o usuário ainda não possui
                .filter(conquista -> !conquista.podeSerDesbloqueadaPor(user)) // E que ainda não pode desbloquear
                .map(conquista -> {
                    return new AchievementProgressDTO(
                            new AchievementDetailsDTO(conquista), // Detalhes da conquista
                            conquista.calcularProgresso(user) // Progresso do usuário
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(conquistasPendentes);
    }

    // Desbloquear conquistas automaticamente para um usuário
    @PostMapping("/desbloquear/{userId}")
    @Transactional
    public ResponseEntity<?> desbloquearConquistas(@PathVariable UUID userId) {
        if (!SecurityUtils.isUserAccessingOwnResource(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var usuario = userRepository.findById(userId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = usuario.get();
        var conquistasDesbloqueaveis = repository.findAll().stream()
                .filter(conquista -> conquista.podeSerDesbloqueadaPor(user))
                .collect(Collectors.toList());

        conquistasDesbloqueaveis.forEach(user::addConquista);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // listar as conquistas desbloqueadas de um usuário
    @GetMapping("/listar/{userId}")
    public ResponseEntity<List<AchievementDetailsDTO>> listarConquistasDesbloqueadas(@PathVariable UUID userId) {
        if (!SecurityUtils.isUserAccessingOwnResource(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var usuario = userRepository.findById(userId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = usuario.get();
        var conquistasDesbloqueadas = user.getConquistas().stream()
                .map(AchievementDetailsDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(conquistasDesbloqueadas);
    }
}