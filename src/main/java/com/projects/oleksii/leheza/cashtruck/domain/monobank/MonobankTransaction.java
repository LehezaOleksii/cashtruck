package com.projects.oleksii.leheza.cashtruck.domain.monobank;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "monobank_transactions")
public class MonobankTransaction {
    @Id
    @SequenceGenerator(name = "monobank_transaction_sequence", sequenceName = "monobank_transaction_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "monobank_transaction_sequence")
    private Long id;
    private String monobankId;
    @OneToOne(fetch = FetchType.EAGER)
    private Transaction transaction;
}
