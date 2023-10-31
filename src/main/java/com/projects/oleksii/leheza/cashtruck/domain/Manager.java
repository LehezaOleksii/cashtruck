package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table
public class Manager {

    @Id
    @SequenceGenerator(name = "client_sequence", sequenceName = "client_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence")
    private Long id;
    @Column(name = "first_name", length = 50)
    private String firstname;
    @Column(name = "last_name", length = 50)
    private String lastname;
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    //TODO add user image
}
