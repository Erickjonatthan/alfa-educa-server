package com.projeto.alfaeduca.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
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
            
            // Configura as opções para usar GPT-4o-mini (mais barato com Vision)
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
                .build();
            
            // Cria o prompt com a mensagem e opções (usando a mensagem diretamente)
            Prompt prompt = new Prompt(userMessage, chatOptions);
            
            // Chama a IA para extrair o texto
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            
            return response != null ? response.trim() : "Nenhum texto encontrado";
            
        } catch (Exception e) {
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
            
            // Configura as opções para usar GPT-4o-mini (mais barato com Vision)
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
                .build();
            
            // Cria o prompt com a mensagem e opções
            Prompt prompt = new Prompt(userMessage, chatOptions);
            
            // Chama a IA para extrair o texto
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            
            return response != null ? response.trim() : "Nenhum texto encontrado";
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar com IA: " + e.getMessage(), e);
        }
    }
}
