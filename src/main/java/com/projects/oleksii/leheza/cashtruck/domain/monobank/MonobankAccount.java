package com.projects.oleksii.leheza.cashtruck.domain.monobank;

import com.projects.oleksii.leheza.cashtruck.domain.Currency;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "monobank_accounts")
public class MonobankAccount {

    @Id
    @SequenceGenerator(name = "monobank_account_sequence", sequenceName = "monobank_account_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "monobank_account_sequence")
    private Long id;
    private String monobankId;
    private long balance;
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;
    private String maskedPan;
    private String type;
    private String holderName;
    private long creditLimit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monobank_integration_id")
    private MonobankIntegration monobankIntegration;

}