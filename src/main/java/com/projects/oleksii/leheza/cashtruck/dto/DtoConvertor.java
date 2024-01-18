package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.domain.Income;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DtoConvertor {

    public IncomeExpensesDto incomeToDto(Income income) {
        return IncomeExpensesDto.builder()
                .sum(income.getTransaction().getSum())
                .name(income.getTransaction().getName())
                .time(income.getTransaction().getTime())
                .category(income.getIncomeCategory().getCategoryName())
                .build();
    }

    public IncomeExpensesDto expenseToDto(Expense expense) {
        return IncomeExpensesDto.builder()
                .sum(expense.getTransaction().getSum())
                .name(expense.getTransaction().getName())
                .time(expense.getTransaction().getTime())
                .category(expense.getExpenseCategory().getCategoryName())
                .build();

    }
    public IncomeExpenseCategoryDto incomeCategoryToDto(Client client, String incomeCategory) {
        List<IncomeExpensesDto> incomes = client.getIncomes().stream()
                .map(this::incomeToDto).toList();
        double totalIncomeSum = incomes
                .stream()
                .mapToDouble(incomeExpensesDto -> incomeExpensesDto.getSum().doubleValue())
                .sum();
        double totalIncomeSumByCategory = incomes.stream()
                .filter(incomeExpensesDto -> incomeExpensesDto.getCategory().equals(incomeCategory))
                .mapToDouble(incomeExpensesDto -> incomeExpensesDto.getSum().doubleValue())
                .sum();
        double categoryPercentage = (totalIncomeSum == 0) ? 0 : (totalIncomeSumByCategory / totalIncomeSum * 100);
        return IncomeExpenseCategoryDto.builder()
                .categoryName(incomeCategory)
                .categoryPercentage(categoryPercentage)
                .fullCategoryTransactionSum(new BigDecimal(totalIncomeSumByCategory))
                .build();
    }

    public IncomeExpenseCategoryDto expenseCategoryToDto(Client client, String expenseCategory) {
        List<IncomeExpensesDto> expenses = client.getExpenses().stream()
                .map(this::expenseToDto).toList();
        double totalExpenseSum = expenses
                .stream()
                .mapToDouble(incomeExpensesDto -> incomeExpensesDto.getSum().doubleValue())
                .sum();
        double totalExpenseSumByCategory = expenses.stream()
                .filter(incomeExpensesDto -> incomeExpensesDto.getCategory().equals(expenseCategory))
                .mapToDouble(incomeExpensesDto -> incomeExpensesDto.getSum().doubleValue())
                .sum();
        double categoryPercentage = (totalExpenseSum == 0) ? 0 : (totalExpenseSumByCategory / totalExpenseSum * 100);
        return IncomeExpenseCategoryDto.builder()
                .categoryName(expenseCategory)
                .categoryPercentage(categoryPercentage)
                .fullCategoryTransactionSum(new BigDecimal(totalExpenseSumByCategory))
                .build();
    }
}
