package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;

import java.util.List;

public interface TransactionService {

//      void createCategory(Category expensesCategory);
//
//      void changeCategory(Long expensesCategoryId, Category expensesCategory);
//
//      void deleteCategory(Long expensesCategoryId);
//
//      List<Category> findAll();

//    void saveIncome(Income income, Long clientId);

    List<Transaction> findAll();

    void save(Transaction transaction); //TODO return Optional/entity

    List<CategoryInfoDto> findClientIncomeCategoriesByClientId(Long clientId);

    List<CategoryInfoDto> findClientExpenseCategoriesByClientId(Long clientId);

    List<TransactionDto> findIncomeTransactionsByClientId(Long clientId);

    List<TransactionDto> findExpenseTransactionsByClientId(Long clientId);

    List<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName);

    List<Transaction> findAllIncomeTransactions();

    List<Transaction> findAllExpenseTransactions();
}
