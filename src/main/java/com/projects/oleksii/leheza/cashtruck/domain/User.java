package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence") // TODO strategy Auto
    private Long id;
    @Column(name = "first_name", length = 50)
    private String firstName;
    @Column(name = "last_name", length = 50)
    private String lastName;
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;
    private String language; //TODO
    private String country; //TODO
    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToOne
    private Image avatar;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_saving"))
    private Saving saving;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_transaction"))
    private List<Transaction> transactions;
    private boolean isEnable; //TODO status
}
