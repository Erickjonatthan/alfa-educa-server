package com.projeto.alfaeduca.domain.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projeto.alfaeduca.domain.conquista.Achievement;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    private byte[] imgPerfil;

    private int nivel;

    private int pontos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "usuario_conquistas",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "conquista_id")
    )
    private List<Achievement> conquistas = new ArrayList<>();

    public void addConquista(Achievement achievement) {
        this.conquistas.add(achievement);
        achievement.getUsuarios().add(this);
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public void setSenha(String senha, PasswordEncoder passwordEncoder) {
        this.senha = passwordEncoder.encode(senha);
    }

    public UserAccount(UserRegistrationData user, PasswordEncoder passwordEncoder, List<String> adminEmails) {
        this.nome = user.nome();
        this.login = user.email();
        this.senha = passwordEncoder.encode(user.senha());
        if (adminEmails.contains(user.email())) {
            this.roles = List.of("ROLE_USER", "ROLE_ADMIN");
        } else {
            this.roles = List.of("ROLE_USER");
        }
    }

    public void atualizarInformacoes(@Valid UserUpdateData dados, PasswordEncoder passwordEncoder) {
        if (dados.nome() != null)
            this.nome = dados.nome();
        if (dados.email() != null)
            this.login = dados.email();
        if (dados.senha() != null)
            this.senha = passwordEncoder.encode(dados.senha());
        if (dados.imgPerfil() != null)
            this.imgPerfil = dados.imgPerfil();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        return roles.contains("ROLE_ADMIN");
    }

    public void adicionarPontos(int pontos) {
        this.pontos += pontos;
        atualizarNivel();
    }

    private void atualizarNivel() {
        int novoNivel = calcularNivel(this.pontos);
        if (novoNivel > this.nivel) {
            this.nivel = novoNivel;
        }
    }
    
    private int calcularNivel(int pontos) {
        return pontos / 100;
    }

}