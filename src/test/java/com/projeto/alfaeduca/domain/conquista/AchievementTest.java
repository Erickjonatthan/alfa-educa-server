package com.projeto.alfaeduca.domain.conquista;

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Usabilidade - Modelo Achievement")
public class AchievementTest {

    private Achievement achievement;
    private UserAccount user;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        // Criar usuário de teste
        var userData = new UserRegistrationData("Usuario Teste", "teste@email.com", "senha123");
        user = new UserAccount(userData, passwordEncoder, List.of());
        
        // Criar conquista de teste
        var achievementData = new AchievementRegistrationData(
            "Conquista Teste",
            "Descrição da conquista teste",
            null,
            5, // nível requerido
            100, // pontos requeridos
            10, // atividades requeridas
            false, // primeira resposta correta
            7 // dias consecutivos
        );
        achievement = new Achievement(achievementData);
    }

    @Nested
    @DisplayName("Testes de Criação de Conquistas")
    class CriacaoConquistasTests {

        @Test
        @DisplayName("Deve criar conquista com todos os campos corretamente")
        void deveCriarConquistaComTodosCampos() {
            assertNotNull(achievement);
            assertEquals("Conquista Teste", achievement.getTitulo());
            assertEquals("Descrição da conquista teste", achievement.getDescricao());
            assertEquals(5, achievement.getNivelRequerido());
            assertEquals(100, achievement.getPontosRequeridos());
            assertEquals(10, achievement.getAtividadesRequeridas());
            assertEquals(false, achievement.getPrimeiraRespostaCorreta());
            assertEquals(7, achievement.getDiasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve criar conquista com campos opcionais nulos")
        void deveCriarConquistaComCamposOpcionaisNulos() {
            var achievementData = new AchievementRegistrationData(
                "Conquista Simples",
                "Conquista apenas com título e descrição",
                null,
                null,
                null,
                null,
                null,
                null
            );
            var conquistaSimples = new Achievement(achievementData);

            assertNotNull(conquistaSimples);
            assertEquals("Conquista Simples", conquistaSimples.getTitulo());
            assertEquals("Conquista apenas com título e descrição", conquistaSimples.getDescricao());
            assertNull(conquistaSimples.getNivelRequerido());
            assertNull(conquistaSimples.getPontosRequeridos());
            assertNull(conquistaSimples.getAtividadesRequeridas());
            assertNull(conquistaSimples.getPrimeiraRespostaCorreta());
            assertNull(conquistaSimples.getDiasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve inicializar lista de usuários vazia")
        void deveInicializarListaUsuariosVazia() {
            assertNotNull(achievement.getUsuarios());
            assertTrue(achievement.getUsuarios().isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes de Verificação de Desbloqueio")
    class VerificacaoDesbloqueioTests {

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário atende critério de nível")
        void devePermitirDesbloqueioComNivelSuficiente() {
            // Configurar usuário com todos os critérios atendidos
            user.setNivel(5);
            user.setPontos(100);
            user.setAtividadesConcluidas(10);
            user.setDiasConsecutivos(7);
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário atende critério de pontos")
        void devePermitirDesbloqueioComPontosSuficientes() {
            // Configurar usuário com todos os critérios atendidos
            user.setNivel(5);
            user.setPontos(100);
            user.setAtividadesConcluidas(10);
            user.setDiasConsecutivos(7);
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário atende critério de atividades")
        void devePermitirDesbloqueioComAtividadesSuficientes() {
            // Configurar usuário com todos os critérios atendidos
            user.setNivel(5);
            user.setPontos(100);
            user.setAtividadesConcluidas(10);
            user.setDiasConsecutivos(7);
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário tem primeira resposta correta")
        void devePermitirDesbloqueioComPrimeiraRespostaCorreta() {
            // Criar conquista que requer primeira resposta correta
            var achievementData = new AchievementRegistrationData(
                "Acertou de Primeira",
                "Acerte na primeira tentativa",
                null,
                null,
                null,
                null,
                true,
                null
            );
            var conquistaPrimeiraResposta = new Achievement(achievementData);
            
            // Configurar usuário com primeira resposta correta
            user.registrarPrimeiraRespostaCorreta();
            
            assertTrue(conquistaPrimeiraResposta.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário atende critério de dias consecutivos")
        void devePermitirDesbloqueioComDiasConsecutivosSuficientes() {
            // Configurar usuário com todos os critérios atendidos
            user.setNivel(5);
            user.setPontos(100);
            user.setAtividadesConcluidas(10);
            user.setDiasConsecutivos(7);
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Não deve permitir desbloqueio quando usuário não atende nenhum critério")
        void naoDevePermitirDesbloqueioSemCriterios() {
            // Usuário com valores insuficientes para todos os critérios
            user.setNivel(1);
            user.setPontos(50);
            user.setAtividadesConcluidas(5);
            user.setDiasConsecutivos(3);
            
            assertFalse(achievement.podeSerDesbloqueadaPor(user));
        }

        @Test
        @DisplayName("Deve permitir desbloqueio quando usuário atende múltiplos critérios")
        void devePermitirDesbloqueioComMultiplosCriterios() {
            // Configurar usuário que atende múltiplos critérios
            user.setNivel(10);
            user.setPontos(200);
            user.setAtividadesConcluidas(20);
            user.setDiasConsecutivos(15);
            user.registrarPrimeiraRespostaCorreta();
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
        }
    }

    @Nested
    @DisplayName("Testes de Cálculo de Progresso")
    class CalculoProgressoTests {

        @Test
        @DisplayName("Deve calcular progresso corretamente para nível insuficiente")
        void deveCalcularProgressoNivelInsuficiente() {
            user.setNivel(3); // precisa de 5
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Faltam 2 níveis para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve calcular progresso corretamente para pontos insuficientes")
        void deveCalcularProgressoPontosInsuficientes() {
            user.setNivel(5); // atende nível
            user.setPontos(70); // precisa de 100
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Faltam 30 pontos para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve calcular progresso corretamente para atividades insuficientes")
        void deveCalcularProgressoAtividadesInsuficientes() {
            user.setNivel(5); // atende nível
            user.setPontos(100); // atende pontos
            user.setAtividadesConcluidas(7); // precisa de 10
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Faltam 3 atividades para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve calcular progresso corretamente para primeira resposta correta")
        void deveCalcularProgressoPrimeiraRespostaCorreta() {
            // Criar conquista que requer primeira resposta correta
            var achievementData = new AchievementRegistrationData(
                "Acertou de Primeira",
                "Acerte na primeira tentativa",
                null,
                null,
                null,
                null,
                true,
                null
            );
            var conquistaPrimeiraResposta = new Achievement(achievementData);
            
            // Usuário que ainda não acertou na primeira
            String progresso = conquistaPrimeiraResposta.calcularProgresso(user);
            
            assertEquals("Você precisa acertar uma resposta na primeira tentativa.", progresso);
        }

        @Test
        @DisplayName("Deve calcular progresso corretamente para dias consecutivos insuficientes")
        void deveCalcularProgressoDiasConsecutivosInsuficientes() {
            user.setNivel(5); // atende nível
            user.setPontos(100); // atende pontos
            user.setAtividadesConcluidas(10); // atende atividades
            user.setDiasConsecutivos(4); // precisa de 7
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Faltam 3 dias consecutivos de login para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve retornar mensagem de conquista desbloqueada quando todos critérios são atendidos")
        void deveRetornarMensagemConquistaDesbloqueada() {
            // Configurar usuário que atende todos os critérios
            user.setNivel(10);
            user.setPontos(200);
            user.setAtividadesConcluidas(20);
            user.setDiasConsecutivos(15);
            user.registrarPrimeiraRespostaCorreta();
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Conquista desbloqueada ou sem progresso necessário.", progresso);
        }

        @Test
        @DisplayName("Deve retornar mensagem padrão para conquista sem critérios")
        void deveRetornarMensagemPadraoSemCriterios() {
            var achievementData = new AchievementRegistrationData(
                "Conquista Livre",
                "Conquista sem critérios específicos",
                null,
                null,
                null,
                null,
                null,
                null
            );
            var conquistaLivre = new Achievement(achievementData);
            
            String progresso = conquistaLivre.calcularProgresso(user);
            
            assertEquals("Conquista desbloqueada ou sem progresso necessário.", progresso);
        }
    }

    @Nested
    @DisplayName("Testes de Casos Extremos")
    class CasosExtremosTests {

        @Test
        @DisplayName("Deve lidar com valores de progresso zero")
        void deveLidarComValoresZero() {
            // Usuário com todos os valores zerados
            user.setNivel(0);
            user.setPontos(0);
            user.setAtividadesConcluidas(0);
            user.setDiasConsecutivos(0);
            
            assertFalse(achievement.podeSerDesbloqueadaPor(user));
            
            String progresso = achievement.calcularProgresso(user);
            assertEquals("Faltam 5 níveis para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve lidar com valores de progresso negativos")
        void deveLidarComValoresNegativos() {
            // Configurar valores negativos (cenário improvável mas possível)
            user.setNivel(-1);
            user.setPontos(-10);
            user.setAtividadesConcluidas(-5);
            user.setDiasConsecutivos(-2);
            
            assertFalse(achievement.podeSerDesbloqueadaPor(user));
            
            String progresso = achievement.calcularProgresso(user);
            assertEquals("Faltam 6 níveis para desbloquear.", progresso);
        }

        @Test
        @DisplayName("Deve lidar com valores muito altos")
        void deveLidarComValoresMuitoAltos() {
            // Configurar valores muito altos
            user.setNivel(1000);
            user.setPontos(10000);
            user.setAtividadesConcluidas(500);
            user.setDiasConsecutivos(365);
            
            assertTrue(achievement.podeSerDesbloqueadaPor(user));
            
            String progresso = achievement.calcularProgresso(user);
            assertEquals("Conquista desbloqueada ou sem progresso necessário.", progresso);
        }

        @Test
        @DisplayName("Deve lidar com conquista que tem apenas um critério")
        void deveLidarComConquistaUmCriterio() {
            var achievementData = new AchievementRegistrationData(
                "Só Nível",
                "Conquista apenas por nível",
                null,
                3,
                null,
                null,
                null,
                null
            );
            var conquistaNivel = new Achievement(achievementData);
            
            user.setNivel(3);
            
            assertTrue(conquistaNivel.podeSerDesbloqueadaPor(user));
            assertEquals("Conquista desbloqueada ou sem progresso necessário.", 
                        conquistaNivel.calcularProgresso(user));
        }
    }

    @Nested
    @DisplayName("Testes de Integração com UserAccount")
    class IntegracaoUserAccountTests {

        @Test
        @DisplayName("Deve manter relacionamento bidirecional com usuário")
        void deveManterRelacionamentoBidirecional() {
            // Adicionar conquista ao usuário
            user.addConquista(achievement);
            
            // Verificar relacionamento bidirecional
            assertTrue(user.getConquistas().contains(achievement));
            assertTrue(achievement.getUsuarios().contains(user));
        }

        @Test
        @DisplayName("Não deve adicionar conquista duplicada ao usuário")
        void naoDeveAdicionarConquistaDuplicada() {
            // Adicionar conquista duas vezes
            user.addConquista(achievement);
            user.addConquista(achievement);
            
            // Deve ter apenas uma ocorrência
            assertEquals(1, user.getConquistas().size());
            assertEquals(1, achievement.getUsuarios().size());
        }

        @Test
        @DisplayName("Deve permitir múltiplos usuários para a mesma conquista")
        void devePermitirMultiplosUsuarios() {
            // Criar segundo usuário
            var userData2 = new UserRegistrationData("Usuario 2", "teste2@email.com", "senha123");
            var user2 = new UserAccount(userData2, passwordEncoder, List.of());
            
            // Adicionar conquista aos dois usuários
            user.addConquista(achievement);
            user2.addConquista(achievement);
            
            // Verificar que ambos têm a conquista
            assertTrue(user.getConquistas().contains(achievement));
            assertTrue(user2.getConquistas().contains(achievement));
            assertEquals(2, achievement.getUsuarios().size());
        }
    }

    @Nested
    @DisplayName("Testes de Usabilidade e Experiência do Usuário")
    class UsabilidadeUXTests {

        @Test
        @DisplayName("Mensagens de progresso devem ser claras e informativas")
        void mensagensProgressoDevemSerClaras() {
            user.setNivel(3);
            user.setPontos(50);
            user.setAtividadesConcluidas(7);
            user.setDiasConsecutivos(5);
            
            String progresso = achievement.calcularProgresso(user);
            
            // Verificar que a mensagem é clara e específica
            assertTrue(progresso.contains("Faltam"));
            assertTrue(progresso.contains("níveis") || progresso.contains("pontos") || 
                      progresso.contains("atividades") || progresso.contains("dias"));
            assertTrue(progresso.contains("desbloquear"));
        }

        @Test
        @DisplayName("Deve priorizar critérios não atendidos na ordem correta")
        void devePriorizarCriteriosNaOrdemCorreta() {
            // Configurar usuário que não atende nível (primeiro critério)
            user.setNivel(3); // não atende (precisa de 5)
            user.setPontos(50); // não atende (precisa de 100)
            user.setAtividadesConcluidas(7); // não atende (precisa de 10)
            
            String progresso = achievement.calcularProgresso(user);
            
            // Deve mostrar o primeiro critério não atendido (nível)
            assertTrue(progresso.contains("níveis"));
            assertFalse(progresso.contains("pontos"));
            assertFalse(progresso.contains("atividades"));
        }

        @Test
        @DisplayName("Deve fornecer feedback motivacional")
        void deveFornecerFeedbackMotivacional() {
            // Usuário próximo de desbloquear
            user.setNivel(4); // falta só 1 nível
            
            String progresso = achievement.calcularProgresso(user);
            
            assertEquals("Faltam 1 níveis para desbloquear.", progresso);
            assertTrue(progresso.toLowerCase().contains("faltam"));
        }
    }
}
