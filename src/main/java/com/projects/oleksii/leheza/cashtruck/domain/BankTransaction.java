package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class BankTransaction {

    @Id
    @SequenceGenerator(name = "bank_transaction_sequence", sequenceName = "bank_transaction_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_transaction_sequence")
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
