package com.projeto.alfaeduca.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> extrairTexto(@RequestParam("file") MultipartFile foto) throws TesseractException{

        File file = new File(foto.getOriginalFilename());
        try {
            foto.transferTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(ocr.extrairTexto(file));
    }
}