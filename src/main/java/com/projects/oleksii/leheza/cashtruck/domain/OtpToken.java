package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "otp_tokens")
public class OtpToken {

    private static int currentPassword = 5487;
    @Id
    @SequenceGenerator(name = "otp_sequence", sequenceName = "otp_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp_sequence")
    private Long id;
    private int password;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public OtpToken(User user) {
        this.user = user;
        currentPassword = (currentPassword >= 9999) ? 1000 : currentPassword + 1;
        password = currentPassword;
    }
}
