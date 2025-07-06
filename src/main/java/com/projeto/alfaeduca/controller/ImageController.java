package com.projeto.alfaeduca.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.alfaeduca.service.AIImageTextService;
import com.projeto.alfaeduca.domain.imagem.SilabaUtils;
import com.projeto.alfaeduca.domain.imagem.DTO.ImagemDetailsDTO;

@RestController
@RequestMapping("/extrair-texto")
public class ImageController {

    @Autowired
    private AIImageTextService aiImageTextService;

    @PostMapping
    public ResponseEntity<?> extrairTexto(@RequestBody Map<String, String> payload) {
        try {
            // Validação da entrada
            String base64Image = payload.get("image");
            if (base64Image == null || base64Image.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Imagem não fornecida", "BAD_REQUEST"));
            }

            // Validação do Base64
            byte[] imageBytes;
            try {
                imageBytes = Base64.getDecoder().decode(base64Image);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(createErrorResponse("Formato Base64 inválido", "INVALID_BASE64"));
            }

            return processarImagem(imageBytes);
            
        } catch (Exception e) {
            // Último recurso para capturar qualquer erro não tratado
            System.err.println("Erro não tratado: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro inesperado no servidor", "UNEXPECTED_ERROR"));
        }
    }

    @PostMapping(value = "/arquivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extrairTextoArquivo(@RequestParam("file") MultipartFile file) {
        try {
            // Validação da entrada
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Arquivo não fornecido", "BAD_REQUEST"));
            }

            // Validação do tipo de arquivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(createErrorResponse("Tipo de arquivo inválido. Apenas imagens são aceitas", "INVALID_FILE_TYPE"));
            }

            // Validação do tamanho
            if (file.getSize() > 20 * 1024 * 1024) { // 20MB
                return ResponseEntity.badRequest().body(createErrorResponse("Arquivo muito grande. Máximo: 20MB", "FILE_TOO_LARGE"));
            }

            byte[] imageBytes = file.getBytes();
            return processarImagem(imageBytes);
            
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao ler arquivo", "FILE_READ_ERROR"));
                
        } catch (Exception e) {
            // Último recurso para capturar qualquer erro não tratado
            System.err.println("Erro não tratado: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro inesperado no servidor", "UNEXPECTED_ERROR"));
        }
    }
    /**
     * Método comum para processar a imagem independente da origem (Base64 ou arquivo)
     */
    private ResponseEntity<?> processarImagem(byte[] imageBytes) {
        try {
            // Validação do tamanho
            if (imageBytes.length > 20 * 1024 * 1024) { // 20MB
                return ResponseEntity.badRequest().body(createErrorResponse("Imagem muito grande. Máximo: 20MB", "IMAGE_TOO_LARGE"));
            }

            String textoExtraido;
            try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                textoExtraido = aiImageTextService.extrairTexto(inputStream);
            } catch (RuntimeException e) {
                // Tratamento específico para erros da IA
                System.err.println("Erro ao processar com IA: " + e.getMessage());
                e.printStackTrace();
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro ao processar imagem com IA: " + e.getMessage(), "AI_PROCESSING_ERROR"));
                    
            } catch (Exception e) {
                // Outros erros
                System.err.println("Erro geral: " + e.getMessage());
                e.printStackTrace();
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor", "INTERNAL_ERROR"));
            }

            // Verifica se foi encontrado texto
            if (textoExtraido == null || textoExtraido.equals("Nenhum texto encontrado") || 
                textoExtraido.contains("NENHUM_TEXTO_ENCONTRADO")) {
                return ResponseEntity.ok().body(new ImagemDetailsDTO("", ""));
            }

            // Remover completamente as quebras de linha do texto extraído
            textoExtraido = textoExtraido.replace("\n", "").replace("\r", "");

            List<String> silabas = SilabaUtils.separarSilabas(textoExtraido);
            String textoSilabado = String.join("-", silabas);

            ImagemDetailsDTO imagemDetailsDTO = new ImagemDetailsDTO(textoExtraido, textoSilabado);
            return ResponseEntity.ok().body(imagemDetailsDTO);
            
        } catch (Exception e) {
            System.err.println("Erro ao processar imagem: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao processar imagem", "IMAGE_PROCESSING_ERROR"));
        }
    }
    
    private Map<String, Object> createErrorResponse(String message, String errorType) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("erro", message);
        errorResponse.put("tipoErro", errorType);
        errorResponse.put("sucesso", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }
}