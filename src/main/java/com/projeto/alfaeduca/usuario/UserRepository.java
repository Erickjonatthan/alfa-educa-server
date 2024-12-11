package com.projeto.alfaeduca.usuario;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public interface UserRepository extends JpaRepository<UserAccount, UUID> {

    
    UserAccount findByLogin(String login);

    boolean existsByLogin(@NotBlank @Email String email);

    
}
