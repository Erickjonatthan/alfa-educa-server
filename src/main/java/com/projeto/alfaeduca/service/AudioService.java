package com.projeto.alfaeduca.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.AudioParameters;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.AudioParameters.Voice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AudioService {

    @Autowired
    private ChatClient chatClient;

    /**
     * Gera áudio do texto normal com velocidade padrão
     */
    public byte[] gerarAudioTextoNormal(String texto) {
        try {
            var userMessage = new UserMessage("Leia o seguinte texto de forma clara e natural: " + texto);

            var response = chatClient.prompt(new Prompt(List.of(userMessage),
                    OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new AudioParameters(Voice.ALLOY, AudioResponseFormat.WAV)) // Voz mais suave
                        .build()))
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null 
                || response.getResult().getOutput().getMedia() == null || response.getResult().getOutput().getMedia().isEmpty()) {
                throw new RuntimeException("Resposta inválida da API OpenAI - áudio não gerado");
            }

            return response.getResult().getOutput().getMedia().get(0).getDataAsByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar áudio do texto normal: " + e.getMessage(), e);
        }
    }

    /**
     * Gera áudio do texto silabado com velocidade mais lenta e pausas entre sílabas
     */
    public byte[] gerarAudioTextoSilabado(String textoSilabado) {
        try {
            // Instruções específicas para leitura silabada mais devagar
            String instrucao = "Leia o seguinte texto silabado de forma muito devagar, " +
                             "fazendo uma pausa pequena entre cada sílaba separada por hífen. " +
                             "Pronuncie cada sílaba de forma clara e distinta. " +
                             "Use um ritmo lento e educativo, como se estivesse ensinando uma criança: " + 
                             textoSilabado;

            var userMessage = new UserMessage(instrucao);

            var response = chatClient.prompt(new Prompt(List.of(userMessage),
                    OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new AudioParameters(Voice.NOVA, AudioResponseFormat.WAV)) // Voz mais suave
                        .build()))
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null 
                || response.getResult().getOutput().getMedia() == null || response.getResult().getOutput().getMedia().isEmpty()) {
                throw new RuntimeException("Resposta inválida da API OpenAI - áudio silabado não gerado");
            }

            return response.getResult().getOutput().getMedia().get(0).getDataAsByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar áudio do texto silabado: " + e.getMessage(), e);
        }
    }

    /**
     * Gera áudios individuais para cada palavra
     */
    public List<byte[]> gerarAudioPalavrasIndividuais(List<String> palavras) {
        List<byte[]> audios = new ArrayList<>();
        
        for (String palavra : palavras) {
            try {
                var userMessage = new UserMessage("Pronuncie apenas esta palavra de forma clara: " + palavra);

                var response = chatClient.prompt(new Prompt(List.of(userMessage),
                        OpenAiChatOptions.builder()
                            .model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                            .outputModalities(List.of("text", "audio"))
                            .outputAudio(new AudioParameters(Voice.ALLOY, AudioResponseFormat.WAV))
                            .build()))
                        .call()
                        .chatResponse();

                if (response == null || response.getResult() == null || response.getResult().getOutput() == null 
                    || response.getResult().getOutput().getMedia() == null || response.getResult().getOutput().getMedia().isEmpty()) {
                    throw new RuntimeException("Resposta inválida da API OpenAI para a palavra: " + palavra);
                }

                audios.add(response.getResult().getOutput().getMedia().get(0).getDataAsByteArray());
                
            } catch (Exception e) {
                System.err.println("Erro ao gerar áudio para a palavra '" + palavra + "': " + e.getMessage());
                // Continua com as outras palavras mesmo se uma falhar
            }
        }
        
        return audios;
    }

    /**
     * Gera áudios individuais para cada palavra silabada (mais devagar)
     */
    public List<byte[]> gerarAudioPalavrasSilabadas(List<String> palavrasSilabas) {
        List<byte[]> audios = new ArrayList<>();
        
        for (String palavraSilabada : palavrasSilabas) {
            try {
                String instrucao = "Pronuncie esta palavra silabada de forma muito devagar, " +
                                 "fazendo uma pequena pausa entre cada sílaba separada por hífen. " +
                                 "Use um ritmo lento e educativo: " + palavraSilabada;

                var userMessage = new UserMessage(instrucao);

                var response = chatClient.prompt(new Prompt(List.of(userMessage),
                        OpenAiChatOptions.builder()
                            .model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                            .outputModalities(List.of("text", "audio"))
                            .outputAudio(new AudioParameters(Voice.NOVA, AudioResponseFormat.WAV))
                            .build()))
                        .call()
                        .chatResponse();

                if (response == null || response.getResult() == null || response.getResult().getOutput() == null 
                    || response.getResult().getOutput().getMedia() == null || response.getResult().getOutput().getMedia().isEmpty()) {
                    throw new RuntimeException("Resposta inválida da API OpenAI para a palavra silabada: " + palavraSilabada);
                }

                audios.add(response.getResult().getOutput().getMedia().get(0).getDataAsByteArray());
                
            } catch (Exception e) {
                System.err.println("Erro ao gerar áudio para a palavra silabada '" + palavraSilabada + "': " + e.getMessage());
                // Continua com as outras palavras mesmo se uma falhar
            }
        }
        
        return audios;
    }
}
