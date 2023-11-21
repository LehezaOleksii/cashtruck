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

    private List<Expense> expenses;
    private List<Income> incomes;
    private BigDecimal totalBalance;
    private BigDecimal lastYearExpense;
    private BigDecimal lastMonthExpense;
    private BigDecimal lastWeekExpense;
    private BigDecimal lastYearIncome;
    private BigDecimal lastMonthIncome;
    private BigDecimal lastWeekIncome;
}
