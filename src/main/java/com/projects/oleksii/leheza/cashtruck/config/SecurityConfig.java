package com.projects.oleksii.leheza.cashtruck.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
//
//    @Value("${security.signing-key}")
//    private String signingKey;
//    @Value("${security.clientSecret}")
//    private String clientSecret;
//    @Value("${security.clientId}")
//    private String clientId;
//    @Value("${security.tokenValidity}")
//    private Integer tokenValidity;
//    @Value("${security.refreshTokenValidity}")
//    private Integer refreshTokenValidity;

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .userDetailsService(userDetailsService);
//                .userDetailsService(userDetailsService)
//                .password(NoOpPasswordEncoder.getInstance())
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                ).oauth2Login(Customizer.withDefaults())
        ;
        return http.build();
    }


//    private ClientRegistration googleClientRegistration() {
//        return ClientRegistration.withRegistrationId("google")
//                .clientId("126136830888-9968uqfmrl5bhcvugsru5v8u46thfvh1.apps.googleusercontent.com")
//                .clientSecret("AIzaSyCdLHZ6ZYe1E0_Mru81FBhYzr6TIxvIOR0")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .scope("openid", "profile", "email", "address", "phone")
//                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .userNameAttributeName(IdTokenClaimNames.SUB)
//                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
//                .clientName("Google")
//                .build();
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable().authorizeRequests()
//                .antMatchers("/oauth/token").permitAll()
//                .antMatchers("/token/token/find-all/**").denyAll()
//                .antMatchers("/token/revoke/refresh-token").authenticated()
//                .anyRequest().authenticated();
//    }
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/v2/api-docs",
//                "/configuration/ui",
//                "/swagger-resources/**",
//                "/configuration/security",
//                "/swagger-ui.html",
//                "/webjars/**");
//    }
}