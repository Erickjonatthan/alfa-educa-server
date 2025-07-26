package com.projeto.alfaeduca.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

       @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> tratarErroAuthentication(AuthenticationException ex) {
        // Adicione logs para depuração
        System.err.println("Erro de autenticação: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErro500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<String> tratarErroValidacao(ValidacaoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarErroArgumentoIlegal(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Argumento inválido: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> tratarErroEstadoIlegal(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Estado inválido: " + ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> tratarErroElementoNaoEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Elemento não encontrado: " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> tratarErroViolacaoRestricao(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Violação de restrição: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> tratarErroViolacaoIntegridadeDados(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Violação de integridade de dados: " + ex.getMessage());
    }

    /**
     * Tratamento específico para quando o parâmetro 'file' não é fornecido
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> tratarParametroAusente(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        if ("file".equals(name)) {
            var errorResponse = new java.util.HashMap<String, Object>();
            errorResponse.put("erro", "Parâmetro 'file' é obrigatório. Certifique-se de enviar o arquivo com o nome 'file' no form-data");
            errorResponse.put("tipoErro", "MISSING_FILE_PARAMETER");
            errorResponse.put("sucesso", false);
            errorResponse.put("timestamp", System.currentTimeMillis());
            errorResponse.put("exemplo", "Use form-data com key='file' e value=seu_arquivo");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        var errorResponse = new java.util.HashMap<String, Object>();
        errorResponse.put("erro", "Parâmetro obrigatório ausente: " + name);
        errorResponse.put("tipoErro", "MISSING_PARAMETER");
        errorResponse.put("sucesso", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}