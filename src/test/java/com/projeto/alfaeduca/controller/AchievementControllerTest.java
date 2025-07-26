package com.projeto.alfaeduca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.alfaeduca.domain.conquista.Achievement;
import com.projeto.alfaeduca.domain.conquista.AchievementRegistrationData;
import com.projeto.alfaeduca.domain.conquista.AchievementRepository;
import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import com.projeto.alfaeduca.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Usabilidade - Sistema de Conquistas")
public class AchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserAccount regularUser;
    private UserAccount adminUser;
    private String userToken;
    private String adminToken;
    private Achievement testAchievement;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste - Ordem importante: primeiro usuários, depois conquistas
        userRepository.deleteAll();
        achievementRepository.deleteAll();

        // Criar usuário regular
        var regularUserData = new UserRegistrationData(
            "Usuario Regular", 
            "usuario@teste.com", 
            "senha123"
        );
        regularUser = new UserAccount(regularUserData, passwordEncoder, List.of());
        regularUser = userRepository.save(regularUser);
        
        var dadosTokenUser = tokenService.gerarToken(regularUser);
        userToken = dadosTokenUser.token();

        // Criar usuário admin
        var adminUserData = new UserRegistrationData(
            "Usuario Admin", 
            "admin@teste.com", 
            "senha123"
        );
        adminUser = new UserAccount(adminUserData, passwordEncoder, List.of("admin@teste.com"));
        adminUser = userRepository.save(adminUser);
        
        var dadosTokenAdmin = tokenService.gerarToken(adminUser);
        adminToken = dadosTokenAdmin.token();

        // Criar conquista de teste
        var achievementData = new AchievementRegistrationData(
            "Primeira Conquista",
            "Conquista de teste para iniciantes",
            null,
            1, // nível requerido
            100, // pontos requeridos
            5, // atividades requeridas
            false,
            null
        );
        testAchievement = new Achievement(achievementData);
        testAchievement = achievementRepository.save(testAchievement);
    }

    @Nested
    @DisplayName("Testes de Cadastro de Conquistas")
    class CadastroConquistasTests {

        @Test
        @DisplayName("Admin deve conseguir cadastrar nova conquista")
        void deveCadastrarNovaConquistaComSucesso() throws Exception {
            var novaConquista = new AchievementRegistrationData(
                "Mestre dos Números",
                "Complete 50 atividades de matemática",
                null,
                5,
                500,
                50,
                false,
                null
            );

            mockMvc.perform(post("/conquista")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(novaConquista))
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.titulo").value("Mestre dos Números"))
                    .andExpect(jsonPath("$.descricao").value("Complete 50 atividades de matemática"))
                    .andExpect(jsonPath("$.nivelRequerido").value(5))
                    .andExpect(jsonPath("$.pontosRequeridos").value(500))
                    .andExpect(jsonPath("$.atividadesRequeridas").value(50));

            // Verificar se foi salva no banco
            var conquistas = achievementRepository.findAll();
            assertEquals(2, conquistas.size()); // 1 do setup + 1 criada
        }

        @Test
        @DisplayName("Usuário comum não deve conseguir cadastrar conquista")
        void usuarioComumNaoDeveCadastrarConquista() throws Exception {
            var novaConquista = new AchievementRegistrationData(
                "Conquista Proibida",
                "Esta conquista não deveria ser criada",
                null,
                1,
                100,
                10,
                false,
                null
            );

            mockMvc.perform(post("/conquista")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(novaConquista))
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Não deve permitir cadastrar conquista sem autenticação")
        void naoDeveCadastrarConquistaSemAutenticacao() throws Exception {
            var novaConquista = new AchievementRegistrationData(
                "Conquista Sem Auth",
                "Esta conquista não deveria ser criada",
                null,
                1,
                100,
                10,
                false,
                null
            );

            mockMvc.perform(post("/conquista")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(novaConquista)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve validar dados obrigatórios ao cadastrar conquista")
        void deveValidarDadosObrigatoriosAoCadastrar() throws Exception {
            var conquistaInvalida = new AchievementRegistrationData(
                "", // título vazio
                "", // descrição vazia
                null,
                null,
                null,
                null,
                null,
                null
            );

            mockMvc.perform(post("/conquista")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(conquistaInvalida))
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes de Listagem de Conquistas")
    class ListagemConquistasTests {

        @Test
        @DisplayName("Admin deve conseguir listar todas as conquistas")
        void adminDeveListarTodasConquistas() throws Exception {
            // Criar mais conquistas
            var conquista2 = new Achievement(new AchievementRegistrationData(
                "Segunda Conquista", "Descrição 2", null, 2, 200, 10, false, null
            ));
            achievementRepository.save(conquista2);

            mockMvc.perform(get("/conquista")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].titulo").value("Primeira Conquista"))
                    .andExpect(jsonPath("$[1].titulo").value("Segunda Conquista"));
        }

        @Test
        @DisplayName("Usuário comum não deve conseguir listar todas as conquistas")
        void usuarioComumNaoDeveListarTodasConquistas() throws Exception {
            mockMvc.perform(get("/conquista")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Usuário deve conseguir listar suas conquistas desbloqueadas")
        void usuarioDeveListarConquistasDesbloqueadas() throws Exception {
            // Adicionar conquista ao usuário
            regularUser.addConquista(testAchievement);
            userRepository.save(regularUser);

            mockMvc.perform(get("/conquista/listar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].titulo").value("Primeira Conquista"));
        }

        @Test
        @DisplayName("Usuário não deve acessar conquistas de outro usuário")
        void usuarioNaoDeveAcessarConquistasDeOutroUsuario() throws Exception {
            mockMvc.perform(get("/conquista/listar/" + adminUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Testes de Verificação de Progresso")
    class VerificacaoProgressoTests {

        @Test
        @DisplayName("Deve calcular progresso corretamente para conquistas pendentes")
        void deveCalcularProgressoCorretamente() throws Exception {
            // Usuário com progresso parcial
            regularUser.setPontos(50); // precisa de 100
            regularUser.setAtividadesConcluidas(2); // precisa de 5
            regularUser.setNivel(1); // já tem o nível necessário
            userRepository.save(regularUser);

            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].conquista.titulo").value("Primeira Conquista"))
                    .andExpect(jsonPath("$[0].progresso").value(containsString("Faltam")));
        }

        @Test
        @DisplayName("Deve mostrar mensagem apropriada quando conquista pode ser desbloqueada")
        void deveMostrarMensagemQuandoConquistaPodeSerDesbloqueada() throws Exception {
            // Usuário que atende todos os requisitos
            regularUser.setPontos(150);
            regularUser.setAtividadesConcluidas(10);
            regularUser.setNivel(2);
            userRepository.save(regularUser);

            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0))); // Nenhuma conquista pendente
        }

        @Test
        @DisplayName("Deve retornar erro para usuário inexistente")
        void deveRetornarErroParaUsuarioInexistente() throws Exception {
            UUID usuarioInexistente = UUID.randomUUID();
            
            mockMvc.perform(get("/conquista/verificar/" + usuarioInexistente)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes de Desbloqueio de Conquistas")
    class DesbloqueioConquistasTests {

        @Test
        @DisplayName("Deve desbloquear conquistas automaticamente quando critérios são atendidos")
        void deveDesbloquearConquistasAutomaticamente() throws Exception {
            // Configurar usuário para atender critérios
            regularUser.setPontos(150);
            regularUser.setAtividadesConcluidas(10);
            regularUser.setNivel(2);
            userRepository.save(regularUser);

            mockMvc.perform(post("/conquista/desbloquear/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            // Verificar se a conquista foi adicionada ao usuário
            var usuarioAtualizado = userRepository.findByIdWithConquistas(regularUser.getId()).orElseThrow();
            assertEquals(1, usuarioAtualizado.getConquistas().size());
            assertEquals("Primeira Conquista", usuarioAtualizado.getConquistas().get(0).getTitulo());
        }

        @Test
        @DisplayName("Não deve desbloquear conquistas quando critérios não são atendidos")
        void naoDeveDesbloquearConquistasSemCriterios() throws Exception {
            // Usuário sem critérios suficientes
            regularUser.setPontos(50);
            regularUser.setAtividadesConcluidas(2);
            regularUser.setNivel(1);
            userRepository.save(regularUser);

            mockMvc.perform(post("/conquista/desbloquear/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            // Verificar que nenhuma conquista foi adicionada
            var usuarioAtualizado = userRepository.findByIdWithConquistas(regularUser.getId()).orElseThrow();
            assertEquals(0, usuarioAtualizado.getConquistas().size());
        }

        @Test
        @DisplayName("Não deve desbloquear conquista já desbloqueada")
        void naoDeveDesbloquearConquistaJaDesbloqueada() throws Exception {
            // Adicionar conquista manualmente primeiro
            regularUser.addConquista(testAchievement);
            regularUser.setPontos(150);
            regularUser.setAtividadesConcluidas(10);
            regularUser.setNivel(2);
            userRepository.save(regularUser);

            mockMvc.perform(post("/conquista/desbloquear/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            // Verificar que ainda tem apenas uma conquista
            var usuarioAtualizado = userRepository.findByIdWithConquistas(regularUser.getId()).orElseThrow();
            assertEquals(1, usuarioAtualizado.getConquistas().size());
        }
    }

    @Nested
    @DisplayName("Testes de Diferentes Tipos de Conquistas")
    class TiposConquistasTests {

        @Test
        @DisplayName("Deve funcionar com conquista baseada em primeira resposta correta")
        void deveFuncionarComConquistaPrimeiraResposta() throws Exception {
            var conquistaPrimeiraResposta = new Achievement(new AchievementRegistrationData(
                "Acertou de Primeira",
                "Acerte uma resposta na primeira tentativa",
                null,
                null,
                null,
                null,
                true,
                null
            ));
            achievementRepository.save(conquistaPrimeiraResposta);

            // Usuário que acertou na primeira
            regularUser.registrarPrimeiraRespostaCorreta();
            userRepository.save(regularUser);

            mockMvc.perform(post("/conquista/desbloquear/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            var usuarioAtualizado = userRepository.findByIdWithConquistas(regularUser.getId()).orElseThrow();
            assertEquals(1, usuarioAtualizado.getConquistas().size());
            assertEquals("Acertou de Primeira", usuarioAtualizado.getConquistas().get(0).getTitulo());
        }

        @Test
        @DisplayName("Deve funcionar com conquista baseada em dias consecutivos")
        void deveFuncionarComConquistaDiasConsecutivos() throws Exception {
            var conquistaDiasConsecutivos = new Achievement(new AchievementRegistrationData(
                "Dedicado",
                "Faça login por 7 dias consecutivos",
                null,
                null,
                null,
                null,
                null,
                7
            ));
            achievementRepository.save(conquistaDiasConsecutivos);

            // Usuário com 7 dias consecutivos
            regularUser.setDiasConsecutivos(7);
            userRepository.save(regularUser);

            mockMvc.perform(post("/conquista/desbloquear/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            var usuarioAtualizado = userRepository.findByIdWithConquistas(regularUser.getId()).orElseThrow();
            assertEquals(1, usuarioAtualizado.getConquistas().size());
            assertEquals("Dedicado", usuarioAtualizado.getConquistas().get(0).getTitulo());
        }

        @Test
        @DisplayName("Deve calcular progresso correto para dias consecutivos")
        void deveCalcularProgressoDiasConsecutivos() throws Exception {
            var conquistaDiasConsecutivos = new Achievement(new AchievementRegistrationData(
                "Persistente",
                "Faça login por 10 dias consecutivos",
                null,
                null,
                null,
                null,
                null,
                10
            ));
            achievementRepository.save(conquistaDiasConsecutivos);

            // Usuário com apenas 5 dias consecutivos
            regularUser.setDiasConsecutivos(5);
            userRepository.save(regularUser);

            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2))) // 2 conquistas pendentes
                    .andExpect(jsonPath("$[*].progresso", hasItem(containsString("5 dias consecutivos"))));
        }
    }

    @Nested
    @DisplayName("Testes de Segurança e Autorização")
    class SegurancaTests {

        @Test
        @DisplayName("Deve bloquear acesso sem token")
        void deveBloquerAcessoSemToken() throws Exception {
            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve bloquear acesso com token inválido")
        void deveBloquerAcessoComTokenInvalido() throws Exception {
            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer tokeninvalido"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Admin deve conseguir acessar dados de qualquer usuário")
        void adminDeveAcessarDadosDeQualquerUsuario() throws Exception {
            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Testes de Usabilidade e UX")
    class UsabilidadeTests {

        @Test
        @DisplayName("Deve retornar mensagens de progresso em português")
        void deveRetornarMensagensEmPortugues() throws Exception {
            // Configurar usuário com progresso parcial
            regularUser.setPontos(50);
            regularUser.setAtividadesConcluidas(2);
            userRepository.save(regularUser);

            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].progresso").value(anyOf(
                        containsString("Faltam"),
                        containsString("pontos"),
                        containsString("atividades")
                    )));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando usuário não tem conquistas")
        void deveRetornarListaVaziaQuandoSemConquistas() throws Exception {
            mockMvc.perform(get("/conquista/listar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Deve manter performance adequada com múltiplas conquistas")
        void deveManterPerformanceComMultiplasConquistas() throws Exception {
            // Criar múltiplas conquistas
            for (int i = 0; i < 20; i++) {
                var conquista = new Achievement(new AchievementRegistrationData(
                    "Conquista " + i,
                    "Descrição " + i,
                    null,
                    i + 1,
                    (i + 1) * 100,
                    (i + 1) * 5,
                    false,
                    null
                ));
                achievementRepository.save(conquista);
            }

            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/conquista/verificar/" + regularUser.getId())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Verificar que a resposta foi rápida (menos de 1 segundo)
            assertTrue(duration < 1000, "Resposta deve ser rápida mesmo com muitas conquistas");
        }
    }
}
