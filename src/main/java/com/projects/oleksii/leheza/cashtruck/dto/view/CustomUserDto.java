package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class CustomUserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String language;
    private String country;
    private String email;
    private String password;
    private UserRole role;
    private String avatar;
}
