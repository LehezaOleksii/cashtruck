package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;

public interface ExpensesCategoryService {
    public void createCategory(ExpensesCategory expensesCategory);

    public void changeCategory(Long expensesCategoryId, ExpensesCategory expensesCategory);

    public void deleteCategory(Long expensesCategoryId);
}
