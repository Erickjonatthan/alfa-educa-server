package com.projeto.alfaeduca.usuario;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String apelido;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(name = "img_url")
    private String imgUrl;
   
    public void setSenha(String senha, PasswordEncoder passwordEncoder) {
        this.senha = passwordEncoder.encode(senha);
    }

    public UserAccount(UserRegistrationData user, PasswordEncoder passwordEncoder) {
        this.nome = user.nome();
        this.login = user.email();
        this.senha =  passwordEncoder.encode(user.senha());
        this.apelido = user.apelido();
        this.imgUrl = user.imgUrl(); 
    }

   

    public void atualizarInformacoes(@Valid UserUpdateData dados, PasswordEncoder passwordEncoder) {
        
        if (dados.nome() != null)
            this.nome = dados.nome();
        if (dados.email() != null)
            this.login = dados.email();
        if (dados.senha() != null)
            this.senha = passwordEncoder.encode(dados.senha());
        if (dados.apelido() != null)
            this.apelido = dados.apelido();
        if (dados.imgUrl() != null)
            this.imgUrl = dados.imgUrl();
        
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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

}