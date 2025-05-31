package com.projeto.alfaeduca.config;

import java.io.InputStream;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
    private ITesseract tesseract = new Tesseract();

    public OCR() {
        // Detecta o sistema operacional e define o caminho apropriado do Tesseract
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            // Caminho padrão do Tesseract no Windows
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        } else {
            // Caminho padrão do Tesseract no Linux/Docker
            tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
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