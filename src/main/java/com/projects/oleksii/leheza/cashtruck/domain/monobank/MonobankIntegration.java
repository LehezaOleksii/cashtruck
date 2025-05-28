package com.projects.oleksii.leheza.cashtruck.domain.monobank;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "monobank_integrations")
public class MonobankIntegration {

    @Id
    @SequenceGenerator(name = "monobank_integration_sequence", sequenceName = "monobank_integration_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "monobank_integration_sequence")
    private Long id;
    private String requestId;
    @OneToMany(mappedBy = "monobankIntegration")
    private Set<MonobankAccount> monobankAccounts;
}
