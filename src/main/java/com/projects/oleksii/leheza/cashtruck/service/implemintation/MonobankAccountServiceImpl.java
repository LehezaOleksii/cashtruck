package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankIntegration;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountDto;
import com.projects.oleksii.leheza.cashtruck.exception.MonobankIntegrationException;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.MonobankAccountService;
import com.projects.oleksii.leheza.cashtruck.util.UkrainianToEnglishNameTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonobankAccountServiceImpl implements MonobankAccountService {

    private final MonobankAccountRepository monobankAccountRepository;
    private final MonobankIntegrationRepository monobankIntegrationRepository;
    private final CurrencyRepository currencyRepository;
    private final DtoMapper dtoMapper;
    private final UkrainianToEnglishNameTranslator ukrainianToEnglishNameTranslator;
    private final MonobankTransactionRepository monobankTransactionRepository;
    private final BankCardRepository bankCardRepository;
    private final CategoryRepository categoryRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void saveMonobankAccounts(Long userId, List<MonobankAccountDto> accountsDto) {
        MonobankIntegration monobankIntegration = monobankIntegrationRepository.findByUserId(userId)
                .orElseThrow(() -> new MonobankIntegrationException("Monobank integration not found for user with id: " + userId));
        Set<MonobankAccount> accounts = accountsDto.stream()
                .map(monobankAccountDto -> dtoMapper.monobankAccountDtoToMonobankAccount(monobankAccountDto, currencyRepository.findByCode(monobankAccountDto.getCurrencyCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Currency not found")), monobankIntegration))
                .collect(Collectors.toSet());
        Set<MonobankAccount> userMonobankAccounts = new HashSet<>(monobankAccountRepository.findByUserId(userId));
        accounts.forEach(account -> {
            boolean isExists = false;
            for (MonobankAccount userMonobankAccount : userMonobankAccounts) {
                if (userMonobankAccount.getMaskedPan().equals(account.getMaskedPan())) {
                    isExists = true;
                    userMonobankAccount.setBalance(account.getBalance());
                    monobankAccountRepository.save(userMonobankAccount);
                }
            }
            if (!isExists) {
                String translatedName = ukrainianToEnglishNameTranslator.transliterate(account.getHolderName());
                String reverseTranslatedName = reverseWords(translatedName);
                account.setHolderName(reverseTranslatedName);
                account = monobankAccountRepository.save(account);
                monobankIntegration.getMonobankAccounts().add(account);
            }
        });
        monobankIntegrationRepository.save(monobankIntegration);
    }

    @Override
    public List<MonobankAccountDto> findByUserId(Long userId) {
        return monobankAccountRepository.findByUserId(userId).stream()
                .map(dtoMapper::monobankAccountToMonobankAccountDto)
                .toList();
    }

    @Override
    public List<MonobankAccount> findByPans(List<String> pans) {
        return monobankAccountRepository.findByPans(pans);
    }

    @Override
    public List<String> getAllTransactionsIds(String monobankId) {
        return monobankTransactionRepository.findTransactionIdsByMonobankId(monobankId);
    }

    @Override
    public void setMonobankAccountBalance(String monobankId, Long userId, Long balance) {
        MonobankAccount monobankAccount = monobankAccountRepository.findByMonobankId(monobankId)
                .orElseThrow(() -> new ResourceNotFoundException("Monobank account not found for id: " + monobankId + "; in setMonobankAccountBalance method"));
        BankCard bankCard = bankCardRepository.findByCardNumber(monobankAccount.getMaskedPan(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("BankCard not found for id: " + monobankId + "; in setMonobankAccountBalance method"));
        Long transactionSum = 0L;
        String cardNumber = bankCard.getCardNumber();
        List<Transaction> transactions = transactionRepository.findByBankCardNumber(cardNumber);
        if (transactions != null && !transactions.isEmpty()) {
            transactionSum = transactions.stream()
                    .map(Transaction::getBankTransaction)
                    .map(BankTransaction::getSum)
                    .mapToLong(Long::longValue)
                    .sum();
        }
        Long firstTransactionSum = balance - transactionSum;
        createFirstTransaction(bankCard, firstTransactionSum);
    }

    private void createFirstTransaction(BankCard bankCard, Long balance) {
        String firstTransactionName = "First transaction";
        Currency currency = bankCard.getCurrency();
        BankTransaction bankTransaction = BankTransaction.builder()
                .currency(currency)
                .name(firstTransactionName)
                .sum(balance)
                .time(LocalDateTime.now())
                .build();
        bankTransactionRepository.save(bankTransaction);
        Category initialCategory = categoryRepository.findByName("Initial card balance")
                .orElseThrow(() -> new ResourceNotFoundException("Initial card balance not found"));
        Transaction firstTransaction = new Transaction();
        firstTransaction.setBankCard(bankCard);
        firstTransaction.setBankTransaction(bankTransaction);
        firstTransaction.setCategory(initialCategory);
        transactionRepository.save(firstTransaction);
    }

    public static String reverseWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder reversed = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            reversed.append(words[i]);
            if (i > 0) reversed.append(" ");
        }

        return reversed.toString();
    }
}
