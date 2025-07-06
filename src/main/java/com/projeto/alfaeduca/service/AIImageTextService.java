package com.projeto.alfaeduca.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Base64;

@Service
public class AIImageTextService {

    @Autowired
    private ChatModel chatModel;

    /**
     * Extrai texto de uma imagem usando IA (OpenAI Vision)
     * @param inputStream InputStream da imagem
     * @return Texto extraído da imagem
     */
    public String extrairTexto(InputStream inputStream) {
        try {
            // Converte o InputStream para bytes
            byte[] imageBytes = inputStream.readAllBytes();
            
            // Validação do tamanho da imagem
            if (imageBytes.length > 20 * 1024 * 1024) { // 20MB máximo
                throw new RuntimeException("Imagem muito grande. Tamanho máximo: 20MB");
            }
            
            // Cria um ByteArrayResource com os bytes da imagem
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            
            // Cria um Media object com a imagem
            Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);
            
            // Cria uma mensagem de usuário com a imagem (usando Builder pattern)
            UserMessage userMessage = UserMessage.builder()
                .text("""
                    Você é um sistema especializado de OCR (Reconhecimento Óptico de Caracteres). 
                    Analise cuidadosamente esta imagem e extraia TODO o texto visível.
                    
                    INSTRUÇÕES ESPECÍFICAS:
                    1. Extraia EXATAMENTE todo o texto que conseguir identificar
                    2. Mantenha a ordem de leitura natural (esquerda para direita, cima para baixo)
                    3. Para texto manuscrito: transcreva da forma mais precisa possível
                    4. Para texto impresso: copie exatamente como está escrito
                    5. Inclua números, símbolos e pontuação
                    6. Se houver várias linhas, separe com espaços (não quebras de linha)
                    7. Ignore elementos gráficos, desenhos ou decorações
                    8. Se não conseguir ler alguma palavra, use [ILEGÍVEL]
                    9. Se não houver texto na imagem, responda apenas: NENHUM_TEXTO_ENCONTRADO
                    
                    IMPORTANTE: Responda APENAS com o texto extraído, sem explicações, comentários ou formatação adicional.
                    """)
                .media(List.of(media))
                .build();
            
            // Configura as opções - usando modelo mais atual  
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")  // Usando modelo mais atual e estável
                .maxTokens(1000)
                .temperature(0.1)  // Baixa temperatura para mais precisão
                .build();
            
            // Cria o prompt com a mensagem e opções
            Prompt prompt = new Prompt(userMessage, chatOptions);
            
            // Chama a IA para extrair o texto
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            
            return response != null ? response.trim() : "Nenhum texto encontrado";
            
        } catch (Exception e) {
            // Log mais detalhado do erro
            System.err.println("Erro detalhado ao processar imagem: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Causa raiz: " + e.getCause().getClass().getSimpleName() + " - " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            // Tenta fallback antes de falhar completamente
            System.out.println("Tentando método de fallback...");
            try {
                String fallbackResult = extrairTextoComFallback(inputStream);
                if (fallbackResult != null && !fallbackResult.equals("NENHUM_TEXTO_ENCONTRADO")) {
                    System.out.println("Fallback bem-sucedido!");
                    return fallbackResult;
                }
            } catch (Exception fallbackException) {
                System.err.println("Fallback também falhou: " + fallbackException.getMessage());
            }
            
            throw new RuntimeException("Erro ao processar imagem com IA: " + e.getMessage(), e);
        }
    }

    /**
     * Extrai texto de uma imagem em base64 usando IA
     * @param base64Image Imagem em formato base64
     * @return Texto extraído da imagem
     */
    public String extrairTextoDeBase64(String base64Image) {
        try {
            // Decodifica a imagem base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            // Validação do tamanho da imagem
            if (imageBytes.length > 20 * 1024 * 1024) { // 20MB máximo
                throw new RuntimeException("Imagem muito grande. Tamanho máximo: 20MB");
            }
            
            // Cria um ByteArrayResource com os bytes da imagem
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            
            // Cria um Media object com a imagem
            Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);
            
            // Cria uma mensagem de usuário com a imagem (usando Builder pattern)
            UserMessage userMessage = UserMessage.builder()
                .text("""
                    Você é um sistema especializado de OCR (Reconhecimento Óptico de Caracteres). 
                    Analise cuidadosamente esta imagem e extraia TODO o texto visível.
                    
                    INSTRUÇÕES ESPECÍFICAS:
                    1. Extraia EXATAMENTE todo o texto que conseguir identificar
                    2. Mantenha a ordem de leitura natural (esquerda para direita, cima para baixo)
                    3. Para texto manuscrito: transcreva da forma mais precisa possível
                    4. Para texto impresso: copie exatamente como está escrito
                    5. Inclua números, símbolos e pontuação
                    6. Se houver várias linhas, separe com espaços (não quebras de linha)
                    7. Ignore elementos gráficos, desenhos ou decorações
                    8. Se não conseguir ler alguma palavra, use [ILEGÍVEL]
                    9. Se não houver texto na imagem, responda apenas: NENHUM_TEXTO_ENCONTRADO
                    
                    IMPORTANTE: Responda APENAS com o texto extraído, sem explicações, comentários ou formatação adicional.
                    """)
                .media(List.of(media))
                .build();
            
            // Configura as opções - usando modelo mais atual
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")  // Usando modelo mais atual e estável
                .maxTokens(1000)
                .temperature(0.1)  // Baixa temperatura para mais precisão
                .build();
            
            // Cria o prompt com a mensagem e opções
            Prompt prompt = new Prompt(userMessage, chatOptions);
            
            // Chama a IA para extrair o texto
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            
            return response != null ? response.trim() : "Nenhum texto encontrado";
            
        } catch (Exception e) {
            // Log mais detalhado do erro
            System.err.println("Erro detalhado ao processar base64: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Causa raiz: " + e.getCause().getClass().getSimpleName() + " - " + e.getCause().getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar com IA: " + e.getMessage(), e);
        }
    }

    /**
     * Método de fallback que tenta diferentes configurações quando a API principal falha
     * @param inputStream InputStream da imagem
     * @return Texto extraído da imagem
     */
    private String extrairTextoComFallback(InputStream inputStream) {
        try {
            // Converte o InputStream para bytes
            byte[] imageBytes = inputStream.readAllBytes();
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);
            
            // Tenta com diferentes modelos/configurações
            String[] modelos = {"gpt-4o", "gpt-4-turbo", "gpt-4"};
            
            for (String modelo : modelos) {
                try {
                    System.out.println("Tentando com modelo: " + modelo);
                    
                    UserMessage userMessage = UserMessage.builder()
                        .text("Extraia todo o texto visível desta imagem. Responda apenas com o texto encontrado.")
                        .media(List.of(media))
                        .build();
                    
                    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                        .model(modelo)
                        .maxTokens(500)  // Reduzindo tokens para tentar evitar problemas
                        .temperature(0.0)  // Temperatura zero para máxima determinação
                        .build();
                    
                    Prompt prompt = new Prompt(userMessage, chatOptions);
                    String response = chatModel.call(prompt).getResult().getOutput().getText();
                    
                    if (response != null && !response.trim().isEmpty()) {
                        System.out.println("Sucesso com modelo: " + modelo);
                        return response.trim();
                    }
                    
                } catch (Exception e) {
                    System.err.println("Falha com modelo " + modelo + ": " + e.getMessage());
                    // Continua para o próximo modelo
                }
            }
            
            return "NENHUM_TEXTO_ENCONTRADO";
            
        } catch (Exception e) {
            System.err.println("Erro no fallback: " + e.getMessage());
            return "NENHUM_TEXTO_ENCONTRADO";
        }
    }

    /**
     * Valida se a configuração da API OpenAI está funcionando
     * @return true se a API estiver funcionando, false caso contrário
     */
    public boolean validarConfiguracaoAPI() {
        try {
            // Cria um prompt simples para testar a API
            UserMessage testMessage = UserMessage.builder()
                .text("Responda apenas 'OK' se você conseguir ler esta mensagem.")
                .build();
            
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .maxTokens(10)
                .temperature(0.0)
                .build();
            
            Prompt prompt = new Prompt(testMessage, chatOptions);
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            
            return response != null && response.trim().toUpperCase().contains("OK");
            
        } catch (Exception e) {
            System.err.println("Erro ao validar configuração da API: " + e.getMessage());
            return false;
        }
    }
}
