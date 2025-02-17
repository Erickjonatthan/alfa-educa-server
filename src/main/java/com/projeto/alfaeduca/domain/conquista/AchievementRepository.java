package com.projeto.alfaeduca.domain.conquista;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

}
