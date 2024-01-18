package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table
public final class IncomeCategory {

    @Id
    @SequenceGenerator(name = "income_category_sequence", sequenceName = "income_category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_category_sequence")
    private Long id;
    @NotBlank
    @NotEmpty
    private String categoryName;
    private String categoryImage;

    public IncomeCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
