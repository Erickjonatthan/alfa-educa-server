package com.projeto.alfaeduca.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AIImageTextServiceTest {

    @MockBean
    private ChatModel chatModel;

    @Autowired
    private AIImageTextService aiImageTextService;

    @BeforeEach
    public void setUp() {
        // Mock de resposta simulada para testes
        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockMessage = mock(AssistantMessage.class);
        
        when(mockMessage.getText()).thenReturn("Texto extraído da imagem de teste");
        when(mockGeneration.getOutput()).thenReturn(mockMessage);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);
    }

    @Test
    public void testExtrairTextoComImagemLocal() {
        try {
            // Criar uma imagem fictícia para teste (dados binários simples)
            byte[] imageBytes = createMockImageBytes();
            InputStream inputStream = new java.io.ByteArrayInputStream(imageBytes);
            
            // Testa a extração de texto
            String textoExtraido = aiImageTextService.extrairTexto(inputStream);
            
            // Verifica se retornou algum resultado
            assertNotNull(textoExtraido, "O texto extraído não deveria ser null");
            assertFalse(textoExtraido.trim().isEmpty(), "O texto extraído não deveria estar vazio");
            
            // Imprime o resultado para análise
            System.out.println("=== TESTE DE EXTRAÇÃO DE TEXTO COM IA ===");
            System.out.println("Imagem: Mock Data");
            System.out.println("Texto extraído: '" + textoExtraido + "'");
            System.out.println("Tamanho do texto: " + textoExtraido.length() + " caracteres");
            System.out.println("==========================================");
            
            // Verifica se não retornou mensagem de erro
            assertFalse(textoExtraido.contains("NENHUM_TEXTO_ENCONTRADO"), 
                "A IA deveria ter encontrado texto na imagem");
            
        } catch (Exception e) {
            fail("Erro ao testar extração de texto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testExtrairTextoComBase64() {
        try {
            // Criar uma imagem fictícia para teste
            byte[] imageBytes = createMockImageBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Testa a extração de texto usando base64
            String textoExtraido = aiImageTextService.extrairTextoDeBase64(base64Image);
            
            // Verifica se retornou algum resultado
            assertNotNull(textoExtraido, "O texto extraído não deveria ser null");
            assertFalse(textoExtraido.trim().isEmpty(), "O texto extraído não deveria estar vazio");
            
            // Imprime o resultado para análise
            System.out.println("=== TESTE DE EXTRAÇÃO DE TEXTO COM BASE64 ===");
            System.out.println("Imagem: Mock Data");
            System.out.println("Tamanho base64: " + base64Image.length() + " caracteres");
            System.out.println("Texto extraído: '" + textoExtraido + "'");
            System.out.println("Tamanho do texto: " + textoExtraido.length() + " caracteres");
            System.out.println("============================================");
            
            // Verifica se não retornou mensagem de erro
            assertFalse(textoExtraido.contains("NENHUM_TEXTO_ENCONTRADO"), 
                "A IA deveria ter encontrado texto na imagem");
            
        } catch (Exception e) {
            fail("Erro ao testar extração de texto com base64: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testComparacaoResultados() {
        try {
            // Criar uma imagem fictícia para teste
            byte[] imageBytes = createMockImageBytes();
            
            // Testa ambos os métodos
            InputStream inputStream = new java.io.ByteArrayInputStream(imageBytes);
            String textoInputStream = aiImageTextService.extrairTexto(inputStream);
            
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String textoBase64 = aiImageTextService.extrairTextoDeBase64(base64Image);
            
            // Compara os resultados
            System.out.println("=== COMPARAÇÃO DE MÉTODOS ===");
            System.out.println("Resultado InputStream: '" + textoInputStream + "'");
            System.out.println("Resultado Base64: '" + textoBase64 + "'");
            System.out.println("Resultados iguais: " + textoInputStream.equals(textoBase64));
            System.out.println("============================");
            
            // Verifica se ambos retornaram resultado válido
            assertNotNull(textoInputStream);
            assertNotNull(textoBase64);
            assertFalse(textoInputStream.trim().isEmpty());
            assertFalse(textoBase64.trim().isEmpty());
            
        } catch (Exception e) {
            fail("Erro ao comparar métodos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cria dados fictícios de uma imagem para testes
     */
    private byte[] createMockImageBytes() {
        // Dados simples que simulam uma imagem pequena
        return new byte[]{
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, // JPEG header
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
            (byte) 0xFF, (byte) 0xD9 // JPEG end
        };
    }
}
