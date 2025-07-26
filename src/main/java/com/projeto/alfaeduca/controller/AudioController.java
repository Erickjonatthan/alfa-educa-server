package com.projeto.alfaeduca.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.alfaeduca.domain.imagem.DTO.ImagemDetailsDTO;
import com.projeto.alfaeduca.service.AudioService;
import com.projeto.alfaeduca.infra.exception.ErrorResponseUtil;

@RestController
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @PostMapping("/texto-normal")
    public ResponseEntity<?> gerarAudioTextoNormal(@RequestBody ImagemDetailsDTO imagemDetails) {
        try {
            // Validação da entrada
            if (imagemDetails == null || imagemDetails.texto() == null || imagemDetails.texto().isEmpty()) {
                return ResponseEntity.badRequest().body(ErrorResponseUtil.createErrorResponse("Texto não fornecido", "BAD_REQUEST"));
            }

            byte[] audioBytes = audioService.gerarAudioTextoNormal(imagemDetails.texto());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"texto_normal.wav\"")
                .body(audioBytes);
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar áudio do texto normal: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseUtil.createErrorResponse("Erro ao gerar áudio", "AUDIO_GENERATION_ERROR"));
        }
    }

    @PostMapping("/texto-silabado")
    public ResponseEntity<?> gerarAudioTextoSilabado(@RequestBody ImagemDetailsDTO imagemDetails) {
        try {
            // Validação da entrada
            if (imagemDetails == null || imagemDetails.textoSilabado() == null || imagemDetails.textoSilabado().isEmpty()) {
                return ResponseEntity.badRequest().body(ErrorResponseUtil.createErrorResponse("Texto silabado não fornecido", "BAD_REQUEST"));
            }

            byte[] audioBytes = audioService.gerarAudioTextoSilabado(imagemDetails.textoSilabado());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"texto_silabado.wav\"")
                .body(audioBytes);
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar áudio do texto silabado: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseUtil.createErrorResponse("Erro ao gerar áudio silabado", "AUDIO_GENERATION_ERROR"));
        }
    }

    @PostMapping("/palavra")
    public ResponseEntity<?> gerarAudioPalavra(@RequestBody Map<String, String> payload) {
        try {
            // Validação da entrada
            String palavra = payload.get("palavra");
            if (palavra == null || palavra.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ErrorResponseUtil.createErrorResponse("Palavra não fornecida", "BAD_REQUEST"));
            }

            byte[] audioBytes = audioService.gerarAudioTextoNormal(palavra.trim());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"palavra.wav\"")
                .body(audioBytes);
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar áudio da palavra: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseUtil.createErrorResponse("Erro ao gerar áudio da palavra", "AUDIO_GENERATION_ERROR"));
        }
    }

    @PostMapping("/palavra-silabada")
    public ResponseEntity<?> gerarAudioPalavraSilabada(@RequestBody Map<String, String> payload) {
        try {
            // Validação da entrada
            String palavraSilabada = payload.get("palavraSilabada");
            if (palavraSilabada == null || palavraSilabada.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ErrorResponseUtil.createErrorResponse("Palavra silabada não fornecida", "BAD_REQUEST"));
            }

            byte[] audioBytes = audioService.gerarAudioTextoSilabado(palavraSilabada.trim());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"palavra_silabada.wav\"")
                .body(audioBytes);
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar áudio da palavra silabada: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseUtil.createErrorResponse("Erro ao gerar áudio da palavra silabada", "AUDIO_GENERATION_ERROR"));
        }
    }
}
