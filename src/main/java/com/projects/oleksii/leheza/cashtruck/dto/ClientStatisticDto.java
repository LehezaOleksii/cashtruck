package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.domain.Income;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
//For UI
public class ClientStatisticDto {
    @Pattern(regexp = "^[0-9]")
    private List<Expense> expenses;
    @Pattern(regexp = "^[0-9]")
    private List<Income> incomes;
}
