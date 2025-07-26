package com.projeto.alfaeduca.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.alfaeduca.service.LLMService;
import com.projeto.alfaeduca.infra.exception.ErrorResponseUtil;

@RestController
@RequestMapping("/llm")
public class LLMController {

    @Autowired
    private LLMService llmService;

    @PostMapping("/send_mensagem")
    public ResponseEntity<?> sendMensagem(@RequestBody Map<String, String> payload) {
        try {
            // Validação da entrada
            String contexto = payload.get("contexto");
            String mensagem = payload.get("mensagem");
            
            if (mensagem == null || mensagem.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ErrorResponseUtil.createErrorResponse("Mensagem não fornecida", "BAD_REQUEST"));
            }
            
            if (contexto == null) {
                contexto = ""; // Contexto vazio se não fornecido
            }

            String resposta = llmService.enviarMensagem(contexto.trim(), mensagem.trim());
            
            return ResponseEntity.ok(Map.of(
                "resposta", resposta,
                "status", "success"
            ));
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para LLM: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseUtil.createErrorResponse("Erro ao processar mensagem", "LLM_ERROR"));
        }
    }
}