package com.projeto.alfaeduca.domain.imagem.DTO;

import java.util.List;

public record ImagemDetailsDTO(
    String texto, 
    String textoSilabado, 
    List<String> palavras, 
    List<String> palavrasSilabas, 
    Integer quantidadePalavras
) {
}