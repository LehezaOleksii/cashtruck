package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "categories")
public final class Category {
    @Id
    @SequenceGenerator(name = "category_sequence", sequenceName = "category_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_sequence")
    private Long id;
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;
    @NotBlank
    @Column(length = 50)
    private String name;

    public Category(TransactionType transactionType, String name) {
        this.transactionType = transactionType;
        this.name = name;
    }
}
