package com.projects.oleksii.leheza.cashtruck.dto.update;

import jakarta.validation.constraints.*;
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
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
    private String status;
    private String avatar;
}