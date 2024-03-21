package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateUserDto {

    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    @Size(min = 7, max = 50)
    private String password;
}
