package com.projeto.alfaeduca.controller;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.alfaeduca.config.OCR;

import net.sourceforge.tess4j.TesseractException;

@RestController
@RequestMapping("/extrair-texto")
public class ImageController {

    @Autowired
    private OCR ocr;
    
    @PostMapping
    public ResponseEntity<String> extrairTexto(@RequestParam("file") MultipartFile foto) throws TesseractException {
        if (foto.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo não enviado ou está vazio.");
        }
    
        String textoExtraido;
        try (InputStream inputStream = foto.getInputStream()) {
            textoExtraido = ocr.extrairTexto(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao extrair texto do arquivo.");
        }
    
        return ResponseEntity.ok().body(textoExtraido);
    }
}