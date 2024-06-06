//package com.projects.oleksii.leheza.cashtruck.config;
//
//import com.projects.oleksii.leheza.cashtruck.enums.Role;
//import com.projects.oleksii.leheza.cashtruck.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService userService;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers("/managers").hasAnyRole(Role.MANAGER.toString(), Role.ADMIN.toString())
//                                .requestMatchers("/admins").hasAnyRole(Role.ADMIN.toString(), Role.CLIENT.toString(), Role.MANAGER.toString())
//                                .anyRequest().authenticated()
//                )
//                .userDetailsService(userService)
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
//
//    @Bean(name = "securityCustomUserDetailsService")
//    public UserDetailsService customUserDetailsService() {
//        return new CustomUserDetailsService();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
