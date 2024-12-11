package com.projeto.alfaeduca.infra.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.projeto.alfaeduca.usuario.UserAccount;

@Component
public class SecurityUtils {

    public static boolean isUserAccessingOwnResource(Long userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            var userDetails = (UserDetails) authentication.getPrincipal();
            var usuario = (UserAccount) userDetails;
            return usuario.getId().equals(userId);
        }
        return false;
    }
}