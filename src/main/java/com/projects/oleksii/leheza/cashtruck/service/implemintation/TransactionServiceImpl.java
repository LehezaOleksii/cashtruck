package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.TransactionRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public List<CategoryInfoDto> findClientIncomeCategoriesByClientId(Long clientId) {
        CustomUser customUser = userRepository.findById(clientId).get();
        List<TransactionDto> transactionDtos = customUser.getTransactions().stream().map(dtoMapper::transactionToDto).toList();
        return categoryRepository.findCategoriesByClientId(INCOME_TRANSACTION_TYPE, clientId).stream()
                .map(category -> dtoMapper.categoryToDtoInfo(transactionDtos, category))
                .toList();
    }

    @Override
    public List<CategoryInfoDto> findClientExpenseCategoriesByClientId(Long clientId) {
        CustomUser customUser = userRepository.findById(clientId).get();
        List<TransactionDto> transactionDtos = customUser.getTransactions().stream().map(dtoMapper::transactionToDto).toList();
        return categoryRepository.findCategoriesByClientId(EXPENSE_TRANSACTION_TYPE, clientId).stream()
                .map(category -> dtoMapper.categoryToDtoInfo(transactionDtos, category))
                .toList();
    }

    @Override
    public List<TransactionDto> findIncomeTransactionsByClientId(Long clientId) {
        return transactionRepository.findTransactionsByClientIdAndTransactionType(clientId, TransactionType.INCOME).stream()
                .map(dtoMapper::transactionToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> findExpenseTransactionsByClientId(Long clientId) {
        return transactionRepository.findTransactionsByClientIdAndTransactionType(clientId, TransactionType.EXPENSE).stream()
                .map(dtoMapper::transactionToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName) {
        return transactionRepository.findTransactionsByClientIdAndCategoryName(clientId, categoryName).stream()
                .map(dtoMapper::transactionToDto)
                .toList();
    }

    @Override
    public List<Transaction> findAllIncomeTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(INCOME_TRANSACTION_TYPE);
    }

    @Override
    public List<Transaction> findAllExpenseTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(EXPENSE_TRANSACTION_TYPE);
    }
}
