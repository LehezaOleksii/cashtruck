package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
//For UI
public class ClientStatisticDto {

    private List<Transaction> expenses;
    private List<Transaction> incomes;
    private BigDecimal totalIncomeSum;
    private BigDecimal totalExpenseSum;
    private BigDecimal totalBalance;
    private BigDecimal lastYearExpense;
    private BigDecimal lastMonthExpense;
    private BigDecimal lastWeekExpense;
    private BigDecimal lastYearIncome;
    private BigDecimal lastMonthIncome;
    private BigDecimal lastWeekIncome;
}
