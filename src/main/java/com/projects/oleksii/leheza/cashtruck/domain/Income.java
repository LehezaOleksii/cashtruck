package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class Income {

    @Id
    @SequenceGenerator(name = "income_sequence", sequenceName = "income_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_sequence")
    private Long id;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_income_category"))
    private IncomeCategory incomeCategory;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_income_transaction"))
    private Transaction transaction;
}
