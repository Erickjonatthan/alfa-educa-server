package com.projeto.alfaeduca.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projeto.alfaeduca.config.OCR;
import com.projeto.alfaeduca.domain.imagem.SilabaUtils;
import com.projeto.alfaeduca.domain.imagem.DTO.ImagemDetailsDTO;

import net.sourceforge.tess4j.TesseractException;

@RestController
@RequestMapping("/extrair-texto")
public class ImageController {

    @Autowired
    private OCR ocr;

    @PostMapping
    public ResponseEntity<ImagemDetailsDTO> extrairTexto(@RequestBody Map<String, String> payload)
            throws TesseractException {
        String base64Image = payload.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

        String textoExtraido;
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            textoExtraido = ocr.extrairTexto(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Remover completamente as quebras de linha do texto extraído
        textoExtraido = textoExtraido.replace("\n", "").replace("\r", "");

        List<String> silabas = SilabaUtils.separarSilabas(textoExtraido);
        String textoSilabado = String.join("-", silabas);

        ImagemDetailsDTO imagemDetailsDTO = new ImagemDetailsDTO(textoExtraido, textoSilabado);
        return ResponseEntity.ok().body(imagemDetailsDTO);
    }
}