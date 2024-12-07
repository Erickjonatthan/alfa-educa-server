package com.projeto.alfaeduca.config;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
    private ITesseract tesseract = new Tesseract();
    

    // public static void main(String[] args) throws TesseractException {
    //     ITesseract tesseract = new Tesseract();

    //     tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

    //     String text = tesseract.doOCR(new File("src\\main\\java\\com\\projeto\\alfaeduca\\controller\\img.png"));

    //     System.out.println(text);
    // }
    public String extrairTexto(File arquivo) throws TesseractException{
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        String texto = tesseract.doOCR(arquivo);
        return texto;
    }
}
