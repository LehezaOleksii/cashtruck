package com.projects.oleksii.leheza.cashtruck.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class Transaction {

    @Id
    @SequenceGenerator(name = "transaction_sequence", sequenceName = "transaction_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_sequence")
    private Long id;
    private String name;
    private LocalDateTime time;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal sum;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_from_bc"))
    private BankCard from;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_to_bc"))
    private BankCard to;
}
