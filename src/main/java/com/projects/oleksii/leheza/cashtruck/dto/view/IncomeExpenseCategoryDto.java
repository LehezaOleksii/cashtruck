package com.projects.oleksii.leheza.cashtruck.dto.view;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class IncomeExpenseCategoryDto {

    @NotNull
    @NotBlank
    private String categoryName;
    private BigDecimal fullCategoryTransactionSum;
    private double categoryPercentage;
}
