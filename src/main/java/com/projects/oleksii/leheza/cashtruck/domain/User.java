package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Enumerated(EnumType.STRING)//???????
    private Role role;
    @OneToOne
    private Image avatar;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_saving"))
    private Saving saving;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_transaction"))
    private List<Transaction> transactions;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ActiveStatus status;

//    public Role getLeadRole() {
//        return roles.stream()
//                .min(comparingInt(Role::getOrder))
//                .orElse(null);
//    }
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return ActiveStatus.isEnabled(status);
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return ActiveStatus.isEnabled(status);
//    }
}
