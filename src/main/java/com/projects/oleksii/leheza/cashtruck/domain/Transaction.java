package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @SequenceGenerator(name = "transaction_sequence", sequenceName = "transaction_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_sequence")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_transaction_bank_transaction"))
    private BankTransaction bankTransaction;
    @ManyToOne
    @JoinColumn(name = "bank_card_id", referencedColumnName = "id")
    private BankCard bankCard;
}
