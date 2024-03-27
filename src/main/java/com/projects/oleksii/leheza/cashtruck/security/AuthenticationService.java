package com.projects.oleksii.leheza.cashtruck.security;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = repository.findByEmailIgnoreCase(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRoles().toString()));

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}