package com.projects.oleksii.leheza.cashtruck.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdminDto {

    @Size(max=50)
    private String firstname;
    @Size(max=50)
    private String lastname;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    @Size(min=7,max=50)
    private String password;
}
