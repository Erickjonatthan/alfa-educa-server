package com.projeto.alfaeduca.config;

import java.io.InputStream;
import javax.imageio.ImageIO;

import io.github.cdimascio.dotenv.Dotenv;

import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
    private ITesseract tesseract = new Tesseract();

    public OCR() {
        Dotenv dotenv = Dotenv.configure().load();
        String tessDataPath = dotenv.get("TESSDATA_PREFIX");
        if (tessDataPath != null) {
            tesseract.setDatapath(tessDataPath);
        } else {
            // Caminho padrão caso a variável de ambiente não esteja definida
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        }
        // Define a linguagem para português
        tesseract.setLanguage("por");
    }

    public String extrairTexto(InputStream inputStream) throws TesseractException {
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (Exception e) {
            throw new TesseractException("Erro ao ler a imagem do InputStream", e);
        }
        return tesseract.doOCR(image);
    }
}