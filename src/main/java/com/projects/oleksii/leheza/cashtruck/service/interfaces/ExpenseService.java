package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;

import java.util.List;

public interface ExpenseService {

    List<Expense> findAll();

    void save(Expense expense);

    List<IncomeExpenseCategoryDto> findClientExpensesCategories(Long clientId);

    List<IncomeExpensesDto> findExpensesByClientId(Long clientId);

}
