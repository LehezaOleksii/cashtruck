package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class Expense {

    @Id
    @SequenceGenerator(name = "expenses_sequence", sequenceName = "expenses_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenses_sequence")
    private Long id;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_expense_category"))
    private ExpensesCategory expenseCategory;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_expenses_transaction"))
    private Transaction transaction;
}
