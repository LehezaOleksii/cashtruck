package com.projects.oleksii.leheza.cashtruck.security;

import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final Faker faker;

    public User saveNewUser(String email) {
        return userService.saveNewUserWithActiveStatus(new LoginDto(email, faker.internet().password(24, 32)));
    }
}