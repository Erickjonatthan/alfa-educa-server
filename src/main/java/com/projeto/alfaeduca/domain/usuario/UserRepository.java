package com.projeto.alfaeduca.domain.usuario;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public interface UserRepository extends JpaRepository<UserAccount, UUID> {

    
    UserAccount findByLogin(String login);

    boolean existsByLogin(@NotBlank @Email String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.conquistas WHERE u.id = :id")
    Optional<UserAccount> findByIdWithConquistas(@Param("id") UUID id);
    
}
