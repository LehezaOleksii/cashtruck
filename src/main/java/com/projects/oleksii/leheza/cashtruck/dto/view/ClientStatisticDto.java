package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import lombok.*;

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
    private long totalIncomeSum;
    private long totalExpenseSum;
    private long totalBalance;
    private long lastYearExpense;
    private long lastMonthExpense;
    private long lastWeekExpense;
    private long lastYearIncome;
    private long lastMonthIncome;
    private long lastWeekIncome;
}
