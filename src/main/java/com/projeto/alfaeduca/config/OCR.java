package com.projeto.alfaeduca.config;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
    private ITesseract tesseract = new Tesseract();
    
    public String extrairTexto(File arquivo) throws TesseractException{
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        String texto = tesseract.doOCR(arquivo);
        return texto;
    }
}
