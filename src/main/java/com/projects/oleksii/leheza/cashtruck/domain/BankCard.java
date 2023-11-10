package com.projects.oleksii.leheza.cashtruck.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.*;
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
    @Column(length = 50)
    private String nameOnCard;
    @Column(length = 3 )
    private String cvv;
    private BigDecimal balance;
    private Date expiringDate;
}
