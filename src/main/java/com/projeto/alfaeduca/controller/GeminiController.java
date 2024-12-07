package com.projeto.alfaeduca.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.projeto.alfaeduca.config.AppConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ia")
public class GeminiController {

    private final RestTemplate restTemplate;

    public GeminiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/generate-content-with-image")
    public ResponseEntity<String> generateContentWithImage(
            @RequestParam("file") MultipartFile file) throws IOException {
        
        // URL do endpoint da API Gemini
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
        

        // {
        //     "contents": [
        //       {
        //         "parts": [
        //           {
        //             "text": "Eai, beleza?"
        //           }
        //         ]
        //       }
        //     ]
        //   }
          
        // Prompt fixo
        String prompt = "Extraia o texto dessa imagem";

        // Criação do corpo da requisição
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(Map.of("text", prompt)));

        // Convertendo a imagem para um array de bytes
        byte[] imageBytes = file.getBytes();
        
        // Criando o corpo da requisição com a imagem
        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("image", imageBytes);
        
        content.put("image", imagePart);

        // Estrutura final da requisição
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("contents", Collections.singletonList(content));

        // Construção da URL com a chave da API
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", AppConfig.getApiKey());

        // Configuração para enviar o multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);

        // Envia a requisição POST para a API do Gemini
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

        return response;
    }
}
