package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
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
        return findClientCategoriesByTransactionType(clientId, INCOME_TRANSACTION_TYPE);
    }

    @Override
    public List<CategoryInfoDto> findClientExpenseCategoriesByClientId(Long clientId) {
        return findClientCategoriesByTransactionType(clientId, EXPENSE_TRANSACTION_TYPE);
    }

    @Override
    public Page<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName, int pageNumber, int pageSize) {
        Pageable pageRequest = createPageRequestUsing(pageNumber, pageSize);
        List<TransactionDto> transactions = transactionRepository.findTransactionsByClientId(clientId).stream()
                .filter(transaction -> transaction.getCategory().getName().equals(categoryName))
                .map(dtoMapper::transactionToDto)
                .collect(Collectors.toList());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), transactions.size());
        List<TransactionDto> pageContent = transactions.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, transactions.size());
    }

    @Override
    public PageDto<TransactionDto> findTransactionsByClientIdAndCategoryName(Long clientId, String categoryName, Integer pageNumber, Integer pageSize) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + clientId + " does not exist"));
        List<TransactionDto> transactions = transactionRepository.findTransactionsByClientId(clientId).stream()
                .filter(transaction -> transaction.getCategory().getName().equals(categoryName))
                .map(dtoMapper::transactionToDto)
                .toList();
        PageDto<TransactionDto> transactionsPageDto = PageDto.<TransactionDto>builder()
                .data(transactions)
                .page(pageNumber)
                .size(pageSize)
                .totalSize(transactions.size())
                .build();
        transactionsPageDto = transactionsPageDto.toBuilder()
                .totalPage((int) Math.ceil((double) transactions.size() / pageSize))
                .build();
        return transactionsPageDto;
    }

    @Override
    public List<Transaction> findAllIncomeTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(INCOME_TRANSACTION_TYPE);
    }

    @Override
    public List<Transaction> findAllExpenseTransactions() {
        return transactionRepository.findTransactionsByCategoryTransactionType(EXPENSE_TRANSACTION_TYPE);
    }

    private List<CategoryInfoDto> findClientCategoriesByTransactionType(Long clientId, TransactionType incomeTransactionType) {
        List<TransactionDto> transactionDtos = transactionRepository.findTransactionsByClientId(clientId).stream().map(dtoMapper::transactionToDto).toList();
        return categoryRepository.findCategoriesByTransactionTypeAndClientId(incomeTransactionType, clientId).stream()
                .map(category -> dtoMapper.categoryToDtoInfo(transactionDtos, category))
                .toList();
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }
}
