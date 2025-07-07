package com.projeto.alfaeduca.infra.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitária para criação de respostas de erro padronizadas
 */
public class ErrorResponseUtil {

    /**
     * Cria uma resposta de erro padronizada
     * 
     * @param message Mensagem de erro
     * @param errorType Tipo do erro
     * @return Map com a estrutura padronizada de erro
     */
    public static Map<String, Object> createErrorResponse(String message, String errorType) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("erro", message);
        errorResponse.put("tipoErro", errorType);
        errorResponse.put("sucesso", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }

    /**
     * Cria uma resposta de erro com informações adicionais
     * 
     * @param message Mensagem de erro
     * @param errorType Tipo do erro
     * @param additionalInfo Informações adicionais para incluir na resposta
     * @return Map com a estrutura padronizada de erro
     */
    public static Map<String, Object> createErrorResponse(String message, String errorType, Map<String, Object> additionalInfo) {
        Map<String, Object> errorResponse = createErrorResponse(message, errorType);
        if (additionalInfo != null) {
            errorResponse.putAll(additionalInfo);
        }
        return errorResponse;
    }
}
