package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

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
    @Column(length = 16)  //Unique TODO
    private String cardNumber;
    @Column(length = 50)
    private String nameOnCard;
    @Column(length = 3)
    private String cvv;
    private BigDecimal balance = BigDecimal.ZERO;
    private Date expiringDate;
}
