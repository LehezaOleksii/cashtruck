package com.projects.oleksii.leheza.cashtruck.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public class Transaction {

    @Id
    @SequenceGenerator(name = "transaction_sequence", sequenceName = "transaction_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_sequence")
    private Long id;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_category"))
    private Category category;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_transaction_bank_transaction"))
    private BankTransaction bankTransaction;
}
