package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class BankCard {

    @Id
    @SequenceGenerator(name = "bankcard_sequence", sequenceName = "bankcard_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bankcard_sequence")
    private Long id;
    @Column(length = 100)
    private String bankName;
    @Column(length = 12)
    private String cardNumber;
    private BigDecimal balance;
    private LocalDateTime expiringDate;
}
