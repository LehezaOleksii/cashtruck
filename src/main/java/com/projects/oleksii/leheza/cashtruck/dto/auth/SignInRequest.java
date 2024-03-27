package com.projects.oleksii.leheza.cashtruck.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SignInRequest {

    @Size(min = 5, max = 255, message = "Length of email must be between 5 and 255")
    @NotBlank(message = "Email does not contain any content")
    @Email(message = "Email should have correct format")
    private String email;
    @Size(min = 3, max = 50, message = "Length of password must be between 3 and 50")
    @NotBlank(message = "Password does not contain any content")
    private String password;
}
