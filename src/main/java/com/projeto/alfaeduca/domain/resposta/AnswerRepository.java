package com.projeto.alfaeduca.domain.resposta;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.alfaeduca.domain.usuario.UserAccount;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    List<Answer> findByUsuario(UserAccount usuario);

    List<Answer> findByUsuarioAndAtividadeId(UserAccount usuario, UUID atividadeId);

}