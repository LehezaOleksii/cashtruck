package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {

    List<Transaction> findAll();

    Transaction save(Transaction transaction); //TODO return Optional/entity

    List<CategoryInfoDto> findClientIncomeCategoriesByClientId(Long clientId);

    List<CategoryInfoDto> findClientExpenseCategoriesByClientId(Long clientId);

    Page<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName, int page,int size);

    PageDto<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName, Integer page, Integer size);

    List<Transaction> findAllIncomeTransactions();

    List<Transaction> findAllExpenseTransactions();
}
