package com.projeto.alfaeduca.domain.conquista;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.projeto.alfaeduca.domain.usuario.UserAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "conquistas")
@Entity(name = "Conquista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Achievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    private byte[] imgConquista;

    @ManyToMany(mappedBy = "conquistas", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserAccount> usuarios = new ArrayList<>();

    public Achievement(AchievementRegistrationData achievementRegistrationData) {
        this.titulo = achievementRegistrationData.titulo();
        this.descricao = achievementRegistrationData.descricao();
        this.imgConquista = achievementRegistrationData.imgConquista();
    }
}