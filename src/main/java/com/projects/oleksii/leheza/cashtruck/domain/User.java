package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence") // TODO strategy Auto
    private Long id;//TODO UUID
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
    @OneToMany
    private Set<Authority> authorities;
    @NotNull
    @Enumerated(EnumType.STRING)//TODO???????
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
    @NotNull
    @ManyToOne
    private Subscription subscription;
    private Date subscriptionFinishDate;
    private BigDecimal balance = new BigDecimal(0);

//            public Role getLeadAuthority() {
//        return roles.stream()
//                .min(comparingInt(Role::getOrder))
//                .orElse(null);
//    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ActiveStatus.isEnabled(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ActiveStatus.isEnabled(status);
    }
}
