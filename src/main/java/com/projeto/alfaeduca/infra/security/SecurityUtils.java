package com.projeto.alfaeduca.infra.security;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.projeto.alfaeduca.domain.usuario.UserAccount;


@Component
public class SecurityUtils {

    public static boolean isUserAccessingOwnResource(UUID userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            var userDetails = (UserDetails) authentication.getPrincipal();
            var usuario = (UserAccount) userDetails;
            return usuario.isAdmin() || usuario.getId().equals(userId);
        }
        return false;
    }

    public static UserAccount getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserAccount) authentication.getPrincipal();
        }
        return null;
    }
}