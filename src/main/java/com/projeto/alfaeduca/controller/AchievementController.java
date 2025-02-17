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

    @PostMapping("/adicionar-ao-usuario/{userId}/{achievementId}")
    @Transactional
    public ResponseEntity<?> adicionarConquistaAoUsuario(@PathVariable UUID userId, @PathVariable UUID achievementId) {
        if (!SecurityUtils.isUserAccessingOwnResource(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var usuario = userRepository.findById(userId);
        var conquista = repository.findById(achievementId);

        if (usuario.isPresent() && conquista.isPresent()) {
            var user = usuario.get();
            var achievement = conquista.get();
            user.addConquista(achievement);
            userRepository.save(user);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<AchievementDetailsDTO>> listarConquistasDoUsuario(@PathVariable UUID userId) {
        if (!SecurityUtils.isUserAccessingOwnResource(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var usuario = userRepository.findById(userId);

        if (usuario.isPresent()) {
            var conquistas = usuario.get().getConquistas();
            var conquistasDTO = conquistas.stream()
                    .map(AchievementDetailsDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(conquistasDTO);
        }

        return ResponseEntity.notFound().build();
    }
}