package com.projeto.alfaeduca.domain.conquista;

import com.projeto.alfaeduca.domain.usuario.UserAccount;
import com.projeto.alfaeduca.domain.usuario.UserRegistrationData;
import com.projeto.alfaeduca.domain.usuario.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Usabilidade - Repository Achievement")
public class AchievementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    private Achievement testAchievement;
    private UserAccount testUser;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        // Criar usuário de teste
        var userData = new UserRegistrationData("Usuario Teste", "teste@email.com", "senha123");
        testUser = new UserAccount(userData, passwordEncoder, List.of());
        testUser = entityManager.persistAndFlush(testUser);

        // Criar conquista de teste
        var achievementData = new AchievementRegistrationData(
            "Conquista Teste",
            "Descrição da conquista teste",
            null,
            5, // nível requerido
            100, // pontos requeridos
            10, // atividades requeridas
            false,
            7 // dias consecutivos
        );
        testAchievement = new Achievement(achievementData);
        testAchievement = entityManager.persistAndFlush(testAchievement);
    }

    @Nested
    @DisplayName("Testes de Operações Básicas CRUD")
    class OperacoesCRUDTests {

        @Test
        @DisplayName("Deve salvar nova conquista corretamente")
        void deveSalvarNovaConquista() {
            var newAchievementData = new AchievementRegistrationData(
                "Nova Conquista",
                "Descrição da nova conquista",
                null,
                3,
                200,
                15,
                true,
                null
            );
            var newAchievement = new Achievement(newAchievementData);

            var savedAchievement = achievementRepository.save(newAchievement);

            assertNotNull(savedAchievement.getId());
            assertEquals("Nova Conquista", savedAchievement.getTitulo());
            assertEquals("Descrição da nova conquista", savedAchievement.getDescricao());
            assertEquals(3, savedAchievement.getNivelRequerido());
            assertEquals(200, savedAchievement.getPontosRequeridos());
            assertEquals(15, savedAchievement.getAtividadesRequeridas());
            assertTrue(savedAchievement.getPrimeiraRespostaCorreta());
            assertNull(savedAchievement.getDiasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve buscar conquista por ID")
        void deveBuscarConquistaPorId() {
            Optional<Achievement> found = achievementRepository.findById(testAchievement.getId());

            assertTrue(found.isPresent());
            assertEquals("Conquista Teste", found.get().getTitulo());
            assertEquals("Descrição da conquista teste", found.get().getDescricao());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio para ID inexistente")
        void deveRetornarOptionalVazioParaIdInexistente() {
            Optional<Achievement> found = achievementRepository.findById(java.util.UUID.randomUUID());

            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("Deve listar todas as conquistas")
        void deveListarTodasConquistas() {
            // Criar mais conquistas
            var achievement2 = new Achievement(new AchievementRegistrationData(
                "Segunda Conquista", "Descrição 2", null, 2, 50, 5, false, null
            ));
            var achievement3 = new Achievement(new AchievementRegistrationData(
                "Terceira Conquista", "Descrição 3", null, 1, 25, 2, true, 3
            ));
            
            entityManager.persistAndFlush(achievement2);
            entityManager.persistAndFlush(achievement3);

            List<Achievement> allAchievements = achievementRepository.findAll();

            assertEquals(3, allAchievements.size());
            assertTrue(allAchievements.stream().anyMatch(a -> a.getTitulo().equals("Conquista Teste")));
            assertTrue(allAchievements.stream().anyMatch(a -> a.getTitulo().equals("Segunda Conquista")));
            assertTrue(allAchievements.stream().anyMatch(a -> a.getTitulo().equals("Terceira Conquista")));
        }

        @Test
        @DisplayName("Deve deletar conquista por ID")
        void deveDeletarConquistaPorId() {
            achievementRepository.deleteById(testAchievement.getId());
            entityManager.flush();

            Optional<Achievement> found = achievementRepository.findById(testAchievement.getId());
            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("Deve atualizar conquista existente")
        void deveAtualizarConquistaExistente() {
            testAchievement.setTitulo("Título Atualizado");
            testAchievement.setDescricao("Descrição Atualizada");
            testAchievement.setPontosRequeridos(300);

            var updatedAchievement = achievementRepository.save(testAchievement);

            assertEquals("Título Atualizado", updatedAchievement.getTitulo());
            assertEquals("Descrição Atualizada", updatedAchievement.getDescricao());
            assertEquals(300, updatedAchievement.getPontosRequeridos());
        }
    }

    @Nested
    @DisplayName("Testes de Integridade de Dados")
    class IntegridadeDadosTests {

        @Test
        @DisplayName("Deve garantir que título seja único")
        void deveGarantirTituloUnico() {
            var achievement1 = new Achievement(new AchievementRegistrationData(
                "Título Único", "Descrição 1", null, 1, 100, 5, false, null
            ));
            var achievement2 = new Achievement(new AchievementRegistrationData(
                "Título Único", "Descrição 2", null, 2, 200, 10, false, null
            ));

            entityManager.persistAndFlush(achievement1);

            // Deve lançar exceção ao tentar salvar outro com mesmo título
            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(achievement2);
            });
        }

        @Test
        @DisplayName("Deve permitir campos opcionais nulos")
        void devePermitirCamposOpcionaisNulos() {
            var achievement = new Achievement(new AchievementRegistrationData(
                "Conquista Mínima",
                "Apenas com campos obrigatórios",
                null,
                null, // nível opcional
                null, // pontos opcional
                null, // atividades opcional
                null, // primeira resposta opcional
                null  // dias consecutivos opcional
            ));

            var savedAchievement = achievementRepository.save(achievement);

            assertNotNull(savedAchievement.getId());
            assertEquals("Conquista Mínima", savedAchievement.getTitulo());
            assertEquals("Apenas com campos obrigatórios", savedAchievement.getDescricao());
            assertNull(savedAchievement.getNivelRequerido());
            assertNull(savedAchievement.getPontosRequeridos());
            assertNull(savedAchievement.getAtividadesRequeridas());
            assertNull(savedAchievement.getPrimeiraRespostaCorreta());
            assertNull(savedAchievement.getDiasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Não deve permitir título vazio ou nulo")
        void naoDevePermitirTituloVazioOuNulo() {
            // Testar título nulo
            var achievementTituloNulo = new Achievement();
            achievementTituloNulo.setTitulo(null);
            achievementTituloNulo.setDescricao("Descrição válida");

            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(achievementTituloNulo);
            });

            // Testar título vazio
            var achievementTituloVazio = new Achievement();
            achievementTituloVazio.setTitulo("");
            achievementTituloVazio.setDescricao("Descrição válida");

            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(achievementTituloVazio);
            });
        }

        @Test
        @DisplayName("Não deve permitir descrição nula")
        void naoDevePermitirDescricaoNula() {
            var achievement = new Achievement();
            achievement.setTitulo("Título Válido");
            achievement.setDescricao(null);

            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(achievement);
            });
        }
    }

    @Nested
    @DisplayName("Testes de Relacionamentos")
    class RelacionamentosTests {

        @Test
        @DisplayName("Deve manter relacionamento Many-to-Many com usuários")
        void deveManterRelacionamentoManyToManyComUsuarios() {
            // Criar segundo usuário
            var userData2 = new UserRegistrationData("Usuario 2", "teste2@email.com", "senha123");
            var user2 = new UserAccount(userData2, passwordEncoder, List.of());
            user2 = entityManager.persistAndFlush(user2);

            // Adicionar conquista aos usuários
            testUser.addConquista(testAchievement);
            user2.addConquista(testAchievement);

            // Salvar usuários
            entityManager.persistAndFlush(testUser);
            entityManager.persistAndFlush(user2);
            entityManager.flush();
            entityManager.clear();

            // Verificar relacionamento
            var achievementFromDb = achievementRepository.findById(testAchievement.getId()).orElseThrow();
            assertEquals(2, achievementFromDb.getUsuarios().size());
            
            var user1FromDb = userRepository.findById(testUser.getId()).orElseThrow();
            var user2FromDb = userRepository.findById(user2.getId()).orElseThrow();
            
            assertTrue(user1FromDb.getConquistas().contains(testAchievement));
            assertTrue(user2FromDb.getConquistas().contains(testAchievement));
        }

        @Test
        @DisplayName("Deve inicializar lista de usuários vazia")
        void deveInicializarListaUsuariosVazia() {
            var achievement = achievementRepository.findById(testAchievement.getId()).orElseThrow();
            
            assertNotNull(achievement.getUsuarios());
            assertTrue(achievement.getUsuarios().isEmpty());
        }

        @Test
        @DisplayName("Deve manter relacionamento ao remover usuário")
        void deveManterRelacionamentoAoRemoverUsuario() {
            // Criar segundo usuário
            var userData2 = new UserRegistrationData("Usuario 2", "teste2@email.com", "senha123");
            var user2 = new UserAccount(userData2, passwordEncoder, List.of());
            user2 = entityManager.persistAndFlush(user2);

            // Adicionar conquista aos usuários
            testUser.addConquista(testAchievement);
            user2.addConquista(testAchievement);
            entityManager.persistAndFlush(testUser);
            entityManager.persistAndFlush(user2);

            // Remover um usuário
            userRepository.delete(testUser);
            entityManager.flush();
            entityManager.clear();

            // Verificar que a conquista ainda existe e tem o outro usuário
            var achievementFromDb = achievementRepository.findByIdWithUsuarios(testAchievement.getId()).orElseThrow();
            assertEquals(1, achievementFromDb.getUsuarios().size());
            assertEquals(user2.getId(), achievementFromDb.getUsuarios().get(0).getId());
        }
    }

    @Nested
    @DisplayName("Testes de Performance e Escalabilidade")
    class PerformanceTests {

        @Test
        @DisplayName("Deve lidar com múltiplas conquistas eficientemente")
        void deveLidarComMultiplasConquistasEficientemente() {
            // Criar múltiplas conquistas
            for (int i = 0; i < 100; i++) {
                var achievement = new Achievement(new AchievementRegistrationData(
                    "Conquista " + i,
                    "Descrição " + i,
                    null,
                    i % 10 + 1,
                    (i + 1) * 10,
                    i % 20 + 1,
                    i % 2 == 0,
                    i % 7 + 1
                ));
                entityManager.persist(achievement);
            }
            entityManager.flush();

            long startTime = System.currentTimeMillis();
            List<Achievement> allAchievements = achievementRepository.findAll();
            long endTime = System.currentTimeMillis();

            assertEquals(101, allAchievements.size()); // 100 criadas + 1 do setup
            assertTrue(endTime - startTime < 1000, "Consulta deve ser rápida mesmo com muitos registros");
        }

        @Test
        @DisplayName("Deve manter performance com relacionamentos complexos")
        void deveManterPerformanceComRelacionamentosComplexos() {
            // Criar múltiplos usuários
            for (int i = 0; i < 20; i++) {
                var userData = new UserRegistrationData("Usuario " + i, "user" + i + "@teste.com", "senha123");
                var user = new UserAccount(userData, passwordEncoder, List.of());
                user = entityManager.persist(user);
                
                // Adicionar conquista ao usuário
                user.addConquista(testAchievement);
                entityManager.persist(user);
            }
            entityManager.flush();

            long startTime = System.currentTimeMillis();
            var achievement = achievementRepository.findById(testAchievement.getId()).orElseThrow();
            int userCount = achievement.getUsuarios().size();
            long endTime = System.currentTimeMillis();

            assertEquals(20, userCount);
            assertTrue(endTime - startTime < 1000, "Consulta com relacionamentos deve ser rápida");
        }
    }

    @Nested
    @DisplayName("Testes de Casos Extremos")
    class CasosExtremosTests {

        @Test
        @DisplayName("Deve lidar com valores extremos nos critérios")
        void deveLidarComValoresExtremosNosCriterios() {
            var achievement = new Achievement(new AchievementRegistrationData(
                "Conquista Extrema",
                "Conquista com valores extremos",
                null,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                true,
                Integer.MAX_VALUE
            ));

            var savedAchievement = achievementRepository.save(achievement);

            assertEquals(Integer.MAX_VALUE, savedAchievement.getNivelRequerido());
            assertEquals(Integer.MAX_VALUE, savedAchievement.getPontosRequeridos());
            assertEquals(Integer.MAX_VALUE, savedAchievement.getAtividadesRequeridas());
            assertEquals(Integer.MAX_VALUE, savedAchievement.getDiasConsecutivosRequeridos());
        }

        @Test
        @DisplayName("Deve lidar com strings muito longas")
        void deveLidarComStringsMuitoLongas() {
            String tituloLongo = "A".repeat(255); // Assumindo que há um limite
            String descricaoLonga = "B".repeat(1000);

            var achievement = new Achievement(new AchievementRegistrationData(
                tituloLongo,
                descricaoLonga,
                null,
                1,
                100,
                5,
                false,
                null
            ));

            // Deve conseguir salvar (ou lançar exceção adequada se há limite)
            assertDoesNotThrow(() -> {
                achievementRepository.save(achievement);
            });
        }

        @Test
        @DisplayName("Deve lidar com imagem binária grande")
        void deveLidarComImagemBinariaGrande() {
            byte[] imagemGrande = new byte[1024 * 1024]; // 1MB
            for (int i = 0; i < imagemGrande.length; i++) {
                imagemGrande[i] = (byte) (i % 256);
            }

            var achievement = new Achievement(new AchievementRegistrationData(
                "Conquista com Imagem",
                "Conquista com imagem grande",
                imagemGrande,
                1,
                100,
                5,
                false,
                null
            ));

            var savedAchievement = achievementRepository.save(achievement);
            
            assertNotNull(savedAchievement.getImgConquista());
            assertEquals(imagemGrande.length, savedAchievement.getImgConquista().length);
        }
    }

    @Nested
    @DisplayName("Testes de Consultas Personalizadas")
    class ConsultasPersonalizadasTests {

        @Test
        @DisplayName("Deve contar conquistas corretamente")
        void deveContarConquistasCorretamente() {
            // Criar mais conquistas
            for (int i = 0; i < 5; i++) {
                var achievement = new Achievement(new AchievementRegistrationData(
                    "Conquista Count " + i,
                    "Descrição " + i,
                    null,
                    i + 1,
                    (i + 1) * 50,
                    i + 1,
                    false,
                    null
                ));
                entityManager.persist(achievement);
            }
            entityManager.flush();

            long count = achievementRepository.count();
            
            assertEquals(6, count); // 5 criadas + 1 do setup
        }

        @Test
        @DisplayName("Deve verificar existência por ID")
        void deveVerificarExistenciaPorId() {
            assertTrue(achievementRepository.existsById(testAchievement.getId()));
            assertFalse(achievementRepository.existsById(java.util.UUID.randomUUID()));
        }
    }
}
