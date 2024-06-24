package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl;
        if (authorities.stream().findFirst().get().getAuthority().equals(Role.ROLE_CLIENT.getAuthority())) {
            redirectUrl = "/clients/dashboard";
        } else {
            redirectUrl = "/managers/dashboard";
        }
        response.sendRedirect(redirectUrl);
    }
}