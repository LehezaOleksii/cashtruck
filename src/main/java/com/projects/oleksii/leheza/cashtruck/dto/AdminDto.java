package com.projects.oleksii.leheza.cashtruck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    @Size(min=7,max=50)
    private String password;
}
