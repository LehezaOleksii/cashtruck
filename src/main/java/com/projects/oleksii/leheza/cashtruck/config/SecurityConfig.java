package com.projects.oleksii.leheza.cashtruck.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles("CLIENT")
                .build());
        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService(passwordEncoder))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/clients/1", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll()
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/clients/**").hasRole("CLIENT")
                        .requestMatchers("/auth/login", "/auth/register", "/js/client/**", "/css/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
