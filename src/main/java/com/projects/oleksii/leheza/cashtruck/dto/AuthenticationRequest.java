package com.projects.oleksii.leheza.cashtruck.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class AuthenticationRequest {

    private String email;
    private String password;
}
