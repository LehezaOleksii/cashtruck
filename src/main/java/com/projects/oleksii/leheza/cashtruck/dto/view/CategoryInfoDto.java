package com.projects.oleksii.leheza.cashtruck.dto.view;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class CategoryInfoDto {

    @NotNull
    @NotBlank
    private String name;
    private double fullCategoryTransactionSum;
    private double categoryPercentage;
    private int currencyCode;
}
