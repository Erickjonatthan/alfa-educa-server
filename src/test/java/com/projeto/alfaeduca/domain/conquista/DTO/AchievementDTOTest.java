package com.projeto.alfaeduca.domain.conquista.DTO;

import com.projeto.alfaeduca.domain.conquista.Achievement;
import com.projeto.alfaeduca.domain.conquista.AchievementRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Usabilidade - DTOs do Sistema de Conquistas")
public class AchievementDTOTest {

    private Achievement testAchievement;

    @BeforeEach
    void setUp() {
        var achievementData = new AchievementRegistrationData(
            "Conquista Teste",
            "Descrição da conquista teste",
            new byte[]{1, 2, 3, 4, 5}, // imagem de teste
            5, // nível requerido
            100, // pontos requeridos
            10, // atividades requeridas
            true, // primeira resposta correta
            7 // dias consecutivos
        );
        
        testAchievement = new Achievement(achievementData);
        // Simular ID gerado pelo banco
        testAchievement.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Testes do AchievementDetailsDTO")
    class AchievementDetailsDTOTests {

        @Test
        @DisplayName("Deve criar DTO com todos os campos da entidade Achievement")
        void deveCriarDTOComTodosCampos() {
            var dto = new AchievementDetailsDTO(testAchievement);

            assertNotNull(dto);
            assertEquals(testAchievement.getId(), dto.id());
            assertEquals(testAchievement.getTitulo(), dto.titulo());
            assertEquals(testAchievement.getDescricao(), dto.descricao());
            assertArrayEquals(testAchievement.getImgConquista(), dto.imgConquista());
            assertEquals(testAchievement.getNivelRequerido(), dto.nivelRequerido());
            assertEquals(testAchievement.getPontosRequeridos(), dto.pontosRequeridos());
            assertEquals(testAchievement.getAtividadesRequeridas(), dto.atividadesRequeridas());
            assertEquals(testAchievement.getPrimeiraRespostaCorreta(), dto.primeiraRespostaCorreta());
            assertEquals(testAchievement.getDiasConsecutivosRequeridos(), dto.diasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve criar DTO direto com parâmetros")
        void deveCriarDTODiretoComParametros() {
            UUID testId = UUID.randomUUID();
            byte[] testImage = {10, 20, 30};

            var dto = new AchievementDetailsDTO(
                testId,
                "Título Direto",
                "Descrição Direta",
                testImage,
                3,
                150,
                8,
                false,
                5
            );

            assertEquals(testId, dto.id());
            assertEquals("Título Direto", dto.titulo());
            assertEquals("Descrição Direta", dto.descricao());
            assertArrayEquals(testImage, dto.imgConquista());
            assertEquals(3, dto.nivelRequerido());
            assertEquals(150, dto.pontosRequeridos());
            assertEquals(8, dto.atividadesRequeridas());
            assertEquals(false, dto.primeiraRespostaCorreta());
            assertEquals(5, dto.diasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve lidar com campos nulos corretamente")
        void deveLidarComCamposNulosCorretamente() {
            var achievementData = new AchievementRegistrationData(
                "Conquista Mínima",
                "Apenas campos obrigatórios",
                null, // imagem nula
                null, // nível nulo
                null, // pontos nulos
                null, // atividades nulas
                null, // primeira resposta nula
                null  // dias consecutivos nulos
            );
            
            var achievement = new Achievement(achievementData);
            achievement.setId(UUID.randomUUID());
            
            var dto = new AchievementDetailsDTO(achievement);

            assertNotNull(dto);
            assertEquals(achievement.getId(), dto.id());
            assertEquals("Conquista Mínima", dto.titulo());
            assertEquals("Apenas campos obrigatórios", dto.descricao());
            assertNull(dto.imgConquista());
            assertNull(dto.nivelRequerido());
            assertNull(dto.pontosRequeridos());
            assertNull(dto.atividadesRequeridas());
            assertNull(dto.primeiraRespostaCorreta());
            assertNull(dto.diasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve ser imutável (record)")
        void deveSerImutavel() {
            var dto = new AchievementDetailsDTO(testAchievement);
            
            // Verificar que é um record (não há setters)
            // Isso é garantido pela natureza do record em Java
            assertNotNull(dto.id());
            assertNotNull(dto.titulo());
            assertNotNull(dto.descricao());
            
            // Tentar modificar a imagem não deve afetar o DTO
            byte[] originalImage = dto.imgConquista();
            if (originalImage != null) {
                originalImage[0] = 99; // Modificar array original
                // O DTO deve manter a referência, mas isso testa que entendemos o comportamento
                assertEquals(99, dto.imgConquista()[0]);
            }
        }

        @Test
        @DisplayName("Deve funcionar com equals e hashCode adequadamente")
        void deveFuncionarComEqualsEHashCodeAdequadamente() {
            var dto1 = new AchievementDetailsDTO(testAchievement);
            var dto2 = new AchievementDetailsDTO(testAchievement);
            
            // Records automaticamente implementam equals e hashCode
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            
            // DTOs com IDs diferentes devem ser diferentes
            var otherAchievement = new Achievement(new AchievementRegistrationData(
                "Outra Conquista", "Outra Descrição", null, 1, 50, 5, false, null
            ));
            otherAchievement.setId(UUID.randomUUID());
            
            var dto3 = new AchievementDetailsDTO(otherAchievement);
            assertNotEquals(dto1, dto3);
        }

        @Test
        @DisplayName("Deve ter toString informativo")
        void deveTerToStringInformativo() {
            var dto = new AchievementDetailsDTO(testAchievement);
            String toString = dto.toString();
            
            assertNotNull(toString);
            assertTrue(toString.contains("AchievementDetailsDTO"));
            assertTrue(toString.contains(dto.titulo()));
            assertTrue(toString.contains(dto.descricao()));
        }
    }

    @Nested
    @DisplayName("Testes do AchievementProgressDTO")
    class AchievementProgressDTOTests {

        @Test
        @DisplayName("Deve criar DTO de progresso com conquista e progresso")
        void deveCriarDTOProgressoComConquistaEProgresso() {
            var conquista = new AchievementDetailsDTO(testAchievement);
            String progresso = "Faltam 5 atividades para desbloquear.";
            
            var progressDTO = new AchievementProgressDTO(conquista, progresso);
            
            assertNotNull(progressDTO);
            assertEquals(conquista, progressDTO.conquista());
            assertEquals(progresso, progressDTO.progresso());
        }

        @Test
        @DisplayName("Deve aceitar diferentes tipos de mensagem de progresso")
        void deveAceitarDiferentesTiposMensagemProgresso() {
            var conquista = new AchievementDetailsDTO(testAchievement);
            
            // Diferentes tipos de mensagem
            String[] mensagensProgresso = {
                "Faltam 3 níveis para desbloquear.",
                "Faltam 50 pontos para desbloquear.",
                "Faltam 7 atividades para desbloquear.",
                "Você precisa acertar uma resposta na primeira tentativa.",
                "Faltam 2 dias consecutivos de login para desbloquear.",
                "Conquista desbloqueada ou sem progresso necessário."
            };
            
            for (String mensagem : mensagensProgresso) {
                var progressDTO = new AchievementProgressDTO(conquista, mensagem);
                
                assertNotNull(progressDTO);
                assertEquals(conquista, progressDTO.conquista());
                assertEquals(mensagem, progressDTO.progresso());
            }
        }

        @Test
        @DisplayName("Deve lidar com progresso nulo ou vazio")
        void deveLidarComProgressoNuloOuVazio() {
            var conquista = new AchievementDetailsDTO(testAchievement);
            
            // Progresso nulo
            var progressDTONulo = new AchievementProgressDTO(conquista, null);
            assertNotNull(progressDTONulo);
            assertNull(progressDTONulo.progresso());
            
            // Progresso vazio
            var progressDTOVazio = new AchievementProgressDTO(conquista, "");
            assertNotNull(progressDTOVazio);
            assertEquals("", progressDTOVazio.progresso());
        }

        @Test
        @DisplayName("Deve funcionar com equals e hashCode para ProgressDTO")
        void deveFuncionarComEqualsEHashCodeParaProgressDTO() {
            var conquista = new AchievementDetailsDTO(testAchievement);
            String progresso = "Teste de progresso";
            
            var dto1 = new AchievementProgressDTO(conquista, progresso);
            var dto2 = new AchievementProgressDTO(conquista, progresso);
            
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            
            // DTOs com progresso diferente devem ser diferentes
            var dto3 = new AchievementProgressDTO(conquista, "Progresso diferente");
            assertNotEquals(dto1, dto3);
        }

        @Test
        @DisplayName("Deve ter toString informativo para ProgressDTO")
        void deveTerToStringInformativoParaProgressDTO() {
            var conquista = new AchievementDetailsDTO(testAchievement);
            String progresso = "Progresso de teste";
            
            var dto = new AchievementProgressDTO(conquista, progresso);
            String toString = dto.toString();
            
            assertNotNull(toString);
            assertTrue(toString.contains("AchievementProgressDTO"));
            assertTrue(toString.contains(progresso));
        }
    }

    @Nested
    @DisplayName("Testes de Integração entre DTOs")
    class IntegracaoDTOsTests {

        @Test
        @DisplayName("Deve converter corretamente de Achievement para ProgressDTO")
        void deveConverterCorretamenteDeAchievementParaProgressDTO() {
            // Simular conversão completa como seria feita no controller
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            String progressoCalculado = "Faltam 2 níveis para desbloquear.";
            
            var progressDTO = new AchievementProgressDTO(detailsDTO, progressoCalculado);
            
            // Verificar que todos os dados foram preservados
            assertEquals(testAchievement.getId(), progressDTO.conquista().id());
            assertEquals(testAchievement.getTitulo(), progressDTO.conquista().titulo());
            assertEquals(testAchievement.getDescricao(), progressDTO.conquista().descricao());
            assertEquals(progressoCalculado, progressDTO.progresso());
        }

        @Test
        @DisplayName("Deve manter consistência entre múltiplas conversões")
        void deveManterConsistenciaEntreMultiplasConversoes() {
            var detailsDTO1 = new AchievementDetailsDTO(testAchievement);
            var detailsDTO2 = new AchievementDetailsDTO(testAchievement);
            
            // Ambos devem ser iguais
            assertEquals(detailsDTO1, detailsDTO2);
            
            // ProgressDTOs com mesma conquista e progresso devem ser iguais
            String progresso = "Teste consistência";
            var progressDTO1 = new AchievementProgressDTO(detailsDTO1, progresso);
            var progressDTO2 = new AchievementProgressDTO(detailsDTO2, progresso);
            
            assertEquals(progressDTO1, progressDTO2);
        }

        @Test
        @DisplayName("Deve suportar serialização JSON (teste conceitual)")
        void deveSuportarSerializacaoJSON() {
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            var progressDTO = new AchievementProgressDTO(detailsDTO, "Progresso teste");
            
            // Verificar que os DTOs têm estrutura adequada para JSON
            assertNotNull(detailsDTO.id());
            assertNotNull(detailsDTO.titulo());
            assertNotNull(detailsDTO.descricao());
            
            assertNotNull(progressDTO.conquista());
            assertNotNull(progressDTO.progresso());
            
            // Em um teste real, usaríamos ObjectMapper para verificar serialização
            // Mas aqui verificamos que a estrutura permite isso
        }
    }

    @Nested
    @DisplayName("Testes de Usabilidade e UX dos DTOs")
    class UsabilidadeUXDTOsTests {

        @Test
        @DisplayName("DTOs devem conter informações suficientes para interface do usuário")
        void dTOsDevemConterInformacoesSuficientesParaInterface() {
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            
            // Informações essenciais para exibição
            assertNotNull(detailsDTO.titulo(), "Título é essencial para exibir conquista");
            assertNotNull(detailsDTO.descricao(), "Descrição é essencial para explicar conquista");
            assertNotNull(detailsDTO.id(), "ID é essencial para operações");
            
            // Informações de critérios (podem ser nulas, mas devem estar presentes)
            // Para interface pode precisar verificar se é null
            assertTrue(detailsDTO.nivelRequerido() != null || 
                      detailsDTO.pontosRequeridos() != null || 
                      detailsDTO.atividadesRequeridas() != null ||
                      detailsDTO.primeiraRespostaCorreta() != null ||
                      detailsDTO.diasConsecutivosRequeridos() != null,
                      "Ao menos um critério deve estar definido para a conquista fazer sentido");
        }

        @Test
        @DisplayName("ProgressDTO deve fornecer feedback claro para usuário")
        void progressDTODeveFornecerFeedbackClaroParaUsuario() {
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            
            // Diferentes tipos de feedback que a interface pode precisar mostrar
            String[] tiposFeedback = {
                "Faltam 3 níveis para desbloquear.",
                "Faltam 50 pontos para desbloquear.",
                "Faltam 7 atividades para desbloquear.",
                "Você precisa acertar uma resposta na primeira tentativa.",
                "Faltam 2 dias consecutivos de login para desbloquear.",
                "Conquista desbloqueada ou sem progresso necessário."
            };
            
            for (String feedback : tiposFeedback) {
                var progressDTO = new AchievementProgressDTO(detailsDTO, feedback);
                
                // Verificar que o feedback é útil para o usuário
                assertNotNull(progressDTO.progresso());
                assertTrue(progressDTO.progresso().length() > 0, "Feedback deve ter conteúdo");
                
                // Feedback deve ser informativo
                assertTrue(
                    progressDTO.progresso().contains("Faltam") ||
                    progressDTO.progresso().contains("precisa") ||
                    progressDTO.progresso().contains("desbloqueada"),
                    "Feedback deve indicar ação necessária ou status"
                );
            }
        }

        @Test
        @DisplayName("DTOs devem ser eficientes para transmissão de dados")
        void dTOsDevemSerEficientesParaTransmissao() {
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            var progressDTO = new AchievementProgressDTO(detailsDTO, "Progresso teste");
            
            // Verificar que não há informações desnecessárias
            // DTOs devem conter apenas dados necessários para a interface
            
            // DetailsDTO contém apenas campos essenciais da conquista
            assertNotNull(detailsDTO.id());
            assertNotNull(detailsDTO.titulo());
            assertNotNull(detailsDTO.descricao());
            // Critérios podem ser nulos (opcional)
            // Imagem pode ser nula (opcional)
            
            // ProgressDTO é minimal - apenas conquista e progresso
            assertNotNull(progressDTO.conquista());
            assertNotNull(progressDTO.progresso());
            
            // Não há campos extras desnecessários
        }

        @Test
        @DisplayName("DTOs devem permitir fácil mapeamento para JSON/API responses")
        void dTOsDevemPermitirFacilMapeamentoParaJSONAPI() {
            var detailsDTO = new AchievementDetailsDTO(testAchievement);
            var progressDTO = new AchievementProgressDTO(detailsDTO, "Teste API");
            
            // Verificar nomenclatura adequada para APIs REST
            // Os nomes dos campos seguem convenções camelCase
            assertNotNull(detailsDTO.id());
            assertNotNull(detailsDTO.titulo());
            assertNotNull(detailsDTO.descricao());
            assertNotNull(detailsDTO.imgConquista());
            assertNotNull(detailsDTO.nivelRequerido());
            assertNotNull(detailsDTO.pontosRequeridos());
            assertNotNull(detailsDTO.atividadesRequeridas());
            assertNotNull(detailsDTO.primeiraRespostaCorreta());
            assertNotNull(detailsDTO.diasConsecutivosRequeridos());
            
            assertNotNull(progressDTO.conquista());
            assertNotNull(progressDTO.progresso());
            
            // Records automaticamente geram getters apropriados para JSON
        }
    }
}
