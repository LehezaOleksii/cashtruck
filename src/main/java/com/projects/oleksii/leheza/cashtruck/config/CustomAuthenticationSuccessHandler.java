package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.implemintation.OAuth2UserService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final OAuth2UserService oAuth2UserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl;
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            String email = oAuth2AuthenticationToken.getPrincipal().getAttributes().get("email").toString();
            if (!userService.existByEmail(email)) {
                oAuth2UserService.saveNewUser(email);
            }
            redirectUrl = "/clients/dashboard";
        } else {
            if (authorities.stream().anyMatch(authority -> authority.getAuthority().equals(Role.ROLE_CLIENT.getAuthority()))) {
                redirectUrl = "/clients/dashboard";
            } else {
                redirectUrl = "/managers/dashboard";
            }
        }
        response.sendRedirect(redirectUrl);
    }
}
