package com.projeto.alfaeduca.controller;

import com.projeto.alfaeduca.service.AIImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/teste-ia")
public class TesteIAController {

    @Autowired
    private AIImageTextService aiImageTextService;

    @GetMapping("/extrair-texto-local")
    public ResponseEntity<Map<String, Object>> testarExtracaoTextoLocal() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Caminho para a imagem de teste
            String imagePath = "C:\\Users\\User\\Documents\\Estudo\\alfa-educa-server\\src\\main\\java\\com\\projeto\\alfaeduca\\config\\img.png";
            
            // Verifica se o arquivo existe
            if (!Files.exists(Paths.get(imagePath))) {
                response.put("sucesso", false);
                response.put("erro", "Arquivo não encontrado: " + imagePath);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Carrega a imagem
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            InputStream inputStream = new java.io.ByteArrayInputStream(imageBytes);
            
            // Mede o tempo de processamento
            long startTime = System.currentTimeMillis();
            String textoExtraido = aiImageTextService.extrairTexto(inputStream);
            long endTime = System.currentTimeMillis();
            
            // Prepara a resposta
            response.put("sucesso", true);
            response.put("arquivo", imagePath);
            response.put("tamanhoArquivo", imageBytes.length);
            response.put("tempoProcessamento", (endTime - startTime) + "ms");
            response.put("textoExtraido", textoExtraido);
            response.put("tamanhoTexto", textoExtraido.length());
            response.put("numeroCaracteres", textoExtraido.length());
            response.put("numeroPalavras", textoExtraido.split("\\s+").length);
            response.put("textoVazio", textoExtraido.trim().isEmpty());
            response.put("contemIlegivel", textoExtraido.contains("[ILEGÍVEL]"));
            response.put("nenhumTextoEncontrado", textoExtraido.contains("NENHUM_TEXTO_ENCONTRADO"));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            response.put("tipoErro", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> verificarStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("servicoIA", "ativo");
            response.put("springAI", "configurado");
            response.put("imagemTeste", Files.exists(Paths.get("C:\\Users\\User\\Documents\\Estudo\\alfa-educa-server\\src\\main\\java\\com\\projeto\\alfaeduca\\config\\img.png")));
            response.put("endpoint", "/teste-ia/extrair-texto-local");
            response.put("instrucoes", "Acesse GET /teste-ia/extrair-texto-local para testar a extração");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
