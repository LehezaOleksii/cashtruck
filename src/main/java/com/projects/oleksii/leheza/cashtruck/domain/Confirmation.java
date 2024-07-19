package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmations")
public class Confirmation {
    @Id
    @SequenceGenerator(name = "token_sequence", sequenceName = "token_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_sequence")
    private Long id;
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public Confirmation(User user) {
        this.user = user;
        createdDate = LocalDateTime.now();
        token = UUID.randomUUID().toString();
    }
}
