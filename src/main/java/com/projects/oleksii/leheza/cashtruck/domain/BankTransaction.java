package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "bank_transactions")
public final class BankTransaction {
    @Id
    @SequenceGenerator(name = "bank_transaction_sequence", sequenceName = "bank_transaction_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_transaction_sequence")
    private Long id;
    private String name;
    private LocalDateTime time;
    @DecimalMin(value = "0")
    private BigDecimal sum;
}
