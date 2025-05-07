package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ClientStatisticDto {

    private List<TransactionDto> expenses;
    private List<TransactionDto> incomes;
    private double totalBalance;
    private double totalBalancePercentage;
    private double lastMonthIncomes;
    private double lastMonthIncomesPercentage;
    private double lastMonthExpenses;
    private double lastMonthExpensesPercentage;
    private double lastMonthProfit;
    private double lastMonthProfitPercentage;
    private double delimiter;
    private Map<String, Long> totalBalanceGraphic;
    private List<DashboardCategoryDto> categoriesDiagram;
    private List<DashboardTransactionDto> lastTransactions;
    private DashboardIncomeExpensesDiagramDto incomeExpensesDiagram;
}
