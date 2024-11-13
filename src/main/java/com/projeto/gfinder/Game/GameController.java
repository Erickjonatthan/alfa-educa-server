package com.projeto.gfinder.Game;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projeto.gfinder.config.AppConfig;

import java.util.Map;

@RestController
@RequestMapping("/jogos")
public class GameController {

    private final RestTemplate restTemplate;

    public GameController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<String> generateContent(@RequestBody Map<String, Object> request) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
        
        // Adiciona a chave da API à URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", AppConfig.getApiKey());

        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), request, String.class);
        return response;
    }
}