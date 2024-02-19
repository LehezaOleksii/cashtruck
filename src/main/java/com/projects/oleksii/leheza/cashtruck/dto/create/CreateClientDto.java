package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateClientDto {

    @Size(max=50)
    private String firstName;
    @Size(max=50)
    private String lastName;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    @Size(min=7,max=50)
    private String password;

}
