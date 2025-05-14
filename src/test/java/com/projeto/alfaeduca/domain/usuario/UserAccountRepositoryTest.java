package com.projeto.alfaeduca.domain.usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserAccountRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void deveCriarUsuarioComSucesso() {
        // Arrange (Preparação)
        var dadosUsuario = new UserRegistrationData(
            "Teste da Silva",
            "teste@email.com",
            "senha123"
        );

        // Act (Ação)
        var usuario = new UserAccount(dadosUsuario, passwordEncoder, java.util.List.of());
        var usuarioSalvo = userRepository.save(usuario);

        // Assert (Verificação)
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Teste da Silva");
        assertThat(usuarioSalvo.getLogin()).isEqualTo("teste@email.com");
        assertThat(passwordEncoder.matches("senha123", usuarioSalvo.getSenha())).isTrue();
    }
}