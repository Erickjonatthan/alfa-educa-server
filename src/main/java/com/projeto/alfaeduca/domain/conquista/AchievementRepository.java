package com.projeto.alfaeduca.domain.conquista;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    @Query("SELECT a FROM Conquista a LEFT JOIN FETCH a.usuarios WHERE a.id = :id")
    Optional<Achievement> findByIdWithUsuarios(@Param("id") UUID id);

}
