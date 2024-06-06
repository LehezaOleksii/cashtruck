package com.projects.oleksii.leheza.cashtruck.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class Saving {

    @Id
    @SequenceGenerator(name = "saving_sequence", sequenceName = "saving_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saving_sequence")
    private Long id;
    private BigDecimal cash;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_saving_bank_card"))
    private Set<BankCard> bankCards;
}