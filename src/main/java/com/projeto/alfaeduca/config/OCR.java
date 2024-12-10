package com.projeto.alfaeduca.config;

import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
    private ITesseract tesseract = new Tesseract();
    

    public String extrairTexto(InputStream inputStream) throws TesseractException {
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (Exception e) {
            throw new TesseractException("Erro ao ler a imagem do InputStream", e);
        }
        return tesseract.doOCR(image);
    }
}