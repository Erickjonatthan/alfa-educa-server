package com.projeto.gfinder.usuario;


import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public interface UserRepository extends JpaRepository<UserAccount, Long> {

    
    UserAccount findByLogin(String login);

    boolean existsByLogin(@NotBlank @Email String email);

    
}
