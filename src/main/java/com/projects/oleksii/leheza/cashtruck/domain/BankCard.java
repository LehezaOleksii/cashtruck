package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "bank_cards")
public final class BankCard {
    @Id
    @SequenceGenerator(name = "bankcard_sequence", sequenceName = "bankcard_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bankcard_sequence")
    private Long id;
    @Column(length = 100, name = "bank_name")
    private String bankName;
    @Column(length = 16, name = "card_number")
    private String cardNumber;
    @Column(length = 50, name = "card_holder")
    private String cardHolder;
    @Column(length = 3)
    private String cvv;
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(name = "expiring_date")
    private Date expiringDate;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @OneToMany(mappedBy = "bankCard")
    private Set<Transaction> transactions = new HashSet<>();
}
