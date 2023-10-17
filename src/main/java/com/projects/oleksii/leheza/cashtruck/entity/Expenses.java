package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class Expenses {

    @Id
    @SequenceGenerator(name = "expenses_sequence", sequenceName = "expenses_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenses_sequence")
    private Long id;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_expense_category"))
    @NotNull
    private ExpensesCategory expenseCategory;
    @NotNull
    private LocalDateTime time;
    @Size(min = 0)
    private BigDecimal sum;
}
