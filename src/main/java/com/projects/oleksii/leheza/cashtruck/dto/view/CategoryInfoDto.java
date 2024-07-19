package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class CategoryInfoDto {

    @NotNull
    @NotBlank
    private String name;
    private BigDecimal fullCategoryTransactionSum;
    private double categoryPercentage;
}
