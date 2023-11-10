package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;

import java.util.List;

public interface ExpensesCategoryService {
      void createCategory(ExpensesCategory expensesCategory);

      void changeCategory(Long expensesCategoryId, ExpensesCategory expensesCategory);

      void deleteCategory(Long expensesCategoryId);

      List<ExpensesCategory> findAll();
}
