package com.projeto.gfinder.preferencia;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import com.projeto.gfinder.usuario.UserAccount;

@Table(name = "preferencias")
@Entity(name = "Preferencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private UserAccount usuario;

    // @OneToOne
    // @JoinColumn(name = "jogo_id", nullable = true)
    // private Jogo jogo;

    @ElementCollection
    private List<String> generos;

    @ElementCollection
    private List<String> plataformas;

    @ElementCollection
    private List<String> modosDeJogo;

}