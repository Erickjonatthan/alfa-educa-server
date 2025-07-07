package com.projeto.alfaeduca.service;

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

            String instrucao = "Repita o seguinte texto de forma clara e natural. Apenas repita o texto, não converse, nem fale nada mais do que isso. ESTE É O TEXTO: "+ texto;
            var userMessage = new UserMessage(instrucao);

            var response = chatClient.prompt(new Prompt(List.of(userMessage),
                    OpenAiChatOptions.builder()
                            .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW)
                            .outputModalities(List.of("text", "audio"))
                            .outputAudio(new AudioParameters(Voice.ALLOY, AudioResponseFormat.WAV)) // Voz mais suave
                            .build()))
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null
                    || response.getResult().getOutput().getMedia() == null
                    || response.getResult().getOutput().getMedia().isEmpty()) {
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
            
            String instrucao = "Leia o seguinte texto de forma silabada.\n"
                    +
                    "TEXTO: " + textoSilabado + "\n\n" +
                    "INSTRUÇÕES SUPER IMPORTANTES ANTES DE GERAR O ÁUDIO:\n" +
                    "Não fale nada além do texto silabado, apenas reproduza o texto.\n" +
                    "Finja que você o um mascote que fala tudo separado por sílabas.\n" +
                    "Você só sabe responder o texto por sílabas.\n" +
                    "Todo texto que você receber, você deve ler separando as sílabas.\n" +
                    "Não tenha medo da letra s, pois você sabe o som de todas as palvras no PT-BR \n";

            var userMessage = new UserMessage(instrucao);

            var response = chatClient.prompt(new Prompt(List.of(userMessage),
                    OpenAiChatOptions.builder()
                            .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW)
                            .outputModalities(List.of("text", "audio"))
                            .outputAudio(new AudioParameters(Voice.NOVA, AudioResponseFormat.WAV)) // Voz mais suave
                            .build()))
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null
                    || response.getResult().getOutput().getMedia() == null
                    || response.getResult().getOutput().getMedia().isEmpty()) {
                throw new RuntimeException("Resposta inválida da API OpenAI - áudio silabado não gerado");
            }

            return response.getResult().getOutput().getMedia().get(0).getDataAsByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar áudio do texto silabado: " + e.getMessage(), e);
        }
    }
}
