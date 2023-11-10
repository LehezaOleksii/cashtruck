package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table
public final class Client {

    @Id
    @SequenceGenerator(name = "client_sequence", sequenceName = "client_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence") //strategy Auto
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
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_saving"))
    private Saving saving;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_income"))
    private List<Income> incomes;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_expense"))
    private List<Expense> expenses;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    //TODO add user image
    //TODO add phone number
}
