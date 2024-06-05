package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.filter.TransactionSpecification;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.TransactionRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    private final TransactionSpecification transactionSpecification;

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<CategoryInfoDto> findClientIncomeCategoriesByClientId(Long clientId) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + clientId + " does not exist"));
        List<TransactionDto> transactionDtos = user.getTransactions().stream().map(dtoMapper::transactionToDto).toList();
        return categoryRepository.findCategoriesByClientId(INCOME_TRANSACTION_TYPE, clientId).stream()
                .map(category -> dtoMapper.categoryToDtoInfo(transactionDtos, category))
                .toList();
    }

    @Override
    public List<CategoryInfoDto> findClientExpenseCategoriesByClientId(Long clientId) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + clientId + " does not exist"));
        List<TransactionDto> transactionDtos = user.getTransactions().stream().map(dtoMapper::transactionToDto).toList();
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
    public Page<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName, int page, int size) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + clientId + " does not exist"));
        Pageable pageRequest = createPageRequestUsing(page, size);
        List<TransactionDto> transactions = user.getTransactions().stream()
                .filter(transaction -> transaction.getCategory().getName().equals(categoryName))
                .map(dtoMapper::transactionToDto)
                .collect(Collectors.toList());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), transactions.size());
        List<TransactionDto> pageContent = transactions.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, transactions.size());
    }


    @Override
    public List<Transaction> findAllIncomeTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(INCOME_TRANSACTION_TYPE);
    }

    @Override
    public List<Transaction> findAllExpenseTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(EXPENSE_TRANSACTION_TYPE);
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }
}
