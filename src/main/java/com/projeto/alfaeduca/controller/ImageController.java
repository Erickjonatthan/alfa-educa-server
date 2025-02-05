package com.projeto.alfaeduca.controller;

import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.projeto.alfaeduca.config.OCR;
import com.projeto.alfaeduca.domain.imagem.SilabaUtils;
import com.projeto.alfaeduca.domain.imagem.DTO.ImagemDetailsDTO;

import net.sourceforge.tess4j.TesseractException;

@RestController
@RequestMapping("/extrair-texto")
public class ImageController {

    @Autowired
    private OCR ocr;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemDetailsDTO> extrairTexto(@RequestParam("file") MultipartFile foto)
            throws TesseractException {
        if (foto.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        String textoExtraido;
        try (InputStream inputStream = foto.getInputStream()) {
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