package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankTransaction;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;
    private static final TransactionType UNIVERSAL_TRANSACTION_TYPE = TransactionType.UNIVERSAL;
    private static final String UNCATEGORIZED_INCOME_CATEGORY_NAME = "Uncategorized income";
    private static final String UNCATEGORIZED_EXPENSE_CATEGORY_NAME = "Uncategorized expense";


    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;
    private final BankCardRepository bankCardRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CurrencyRepository currencyRepository;
    private final MonobankTransactionRepository monobankTransactionRepository;

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction save(MonobankAccountTransactionDto monobankAccountTransactionDto, String bankCardNumber, Long userId) {
        BankCard bankCard = bankCardRepository.findCardByNumberAndUserId(bankCardNumber, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank card with bank card number: " + bankCardNumber + " and user id: " + userId + " not found"));
        Currency currency = currencyRepository.findByCode(Integer.parseInt(monobankAccountTransactionDto.getCurrencyCode()))
                .orElseThrow(() -> new ResourceNotFoundException("Currency with currency code: " + monobankAccountTransactionDto.getCurrencyCode() + " not found"));
        BankTransaction bankTransaction = new BankTransaction().toBuilder()
                .sum(monobankAccountTransactionDto.getAmount())
                .name(monobankAccountTransactionDto.getDescription())
                .time(Instant.ofEpochSecond(monobankAccountTransactionDto.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .currency(currency)
                .build();
        bankTransaction = bankTransactionRepository.save(bankTransaction);
        Optional<Category> categoryOptional = categoryRepository.findByMcc(monobankAccountTransactionDto.getMcc());
        Category category;
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
        } else {
            if (monobankAccountTransactionDto.getAmount() > 0) {
                category = categoryRepository.findByName(UNCATEGORIZED_INCOME_CATEGORY_NAME).get();
            } else {
                category = categoryRepository.findByName(UNCATEGORIZED_EXPENSE_CATEGORY_NAME).get();
            }
        }
        Transaction transaction = dtoMapper.MonobankAccountTransactionDto(monobankAccountTransactionDto, bankCard, bankTransaction, category);
        transactionRepository.save(transaction);
        return transaction;
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

    private List<CategoryInfoDto> findClientCategoriesByTransactionType(Long clientId, TransactionType transactionType) {
        List<TransactionDto> transactionDtos = transactionRepository.findTransactionsByClientId(clientId).stream().map(dtoMapper::transactionToDto).toList();
        List<TransactionType> transactionTypes = new ArrayList<>(
                List.of(transactionType, UNIVERSAL_TRANSACTION_TYPE));
        boolean isPositiveTransactionSum;
        if (transactionType.equals(INCOME_TRANSACTION_TYPE)) {
            isPositiveTransactionSum = true;
        } else {
            isPositiveTransactionSum = false;
        }
        List<Category> userCategories = categoryRepository.findCategoriesByTransactionTypesAndClientId(transactionTypes, clientId, isPositiveTransactionSum).stream()
                .toList();
        return userCategories.stream()
                .flatMap(category -> dtoMapper.categoryToDtoInfo(transactionDtos, category, isPositiveTransactionSum).stream())
                .collect(Collectors.toList());
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }
}
