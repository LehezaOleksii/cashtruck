package com.projects.oleksii.leheza.cashtruck.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserUpdateDto {

    private Long id;
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;
    @NotEmpty
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
    private String avatar;
}