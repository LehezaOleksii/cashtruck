package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankIntegration;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankTransaction;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.*;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DtoMapper {

    private static final int HRYVNIA_CURRENCY_CODE = 980;
    private final ImageConvertor imageConvertor;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

    public TransactionDto transactionToDto(Transaction transaction) {
        Currency currency = transaction.getBankTransaction().getCurrency();
        return TransactionDto.builder()
                .sum((double) transaction.getBankTransaction().getSum() / currency.getDelimiter())
                .name(transaction.getBankTransaction().getName())
                .time(transaction.getBankTransaction().getTime())
                .category(transaction.getCategory().getName())
                .transactionType(transaction.getCategory().getTransactionType().toString())
                .currencyCode(currency.getCode())
                .delimiter(currency.getDelimiter())
                .build();
    }

    public List<CategoryInfoDto> categoryToDtoInfo(List<TransactionDto> transactionDtos, Category category, boolean isTransactionPositive) {
        String categoryName = category.getName();

        List<TransactionDto> categoryTransactions = transactionDtos.stream()
                .filter(transactionDto -> transactionDto.getCategory().equals(categoryName))
                .filter(transactionDto -> isTransactionPositive ? transactionDto.getSum() > 0 : transactionDto.getSum() < 0)
                .toList();

        Map<Integer, List<TransactionDto>> transactionsByCurrency = categoryTransactions.stream()
                .collect(Collectors.groupingBy(TransactionDto::getCurrencyCode));

        double totalSum = transactionDtos.stream()
                .filter(transactionDto -> isTransactionPositive ? transactionDto.getSum() > 0 : transactionDto.getSum() < 0)
                .mapToDouble(TransactionDto::getSum)
                .sum();

        return transactionsByCurrency.entrySet().stream()
                .map(entry -> {
                    int currency = entry.getKey();
                    List<TransactionDto> currencyTransactions = entry.getValue();

                    double totalSumByCategory = currencyTransactions.stream()
                            .mapToDouble(t -> t.getSum())
                            .sum();

                    double totalSumByCategoryAndCurrency = currencyTransactions.stream()
                            .filter(t -> t.getCurrencyCode() == 980)
                            .mapToDouble(t -> t.getSum())
                            .sum();

                    double categoryPercentage = (totalSum == 0) ? 0
                            : (totalSumByCategoryAndCurrency * 100 / totalSum);

                    return CategoryInfoDto.builder()
                            .name(categoryName)
                            .categoryPercentage(categoryPercentage)
                            .fullCategoryTransactionSum(totalSumByCategory)
                            .currencyCode(currency)
                            .build();
                })
                .toList();
    }


    public CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .transactionType(category.getTransactionType())
                .name(category.getName())
                .build();
    }

    public CreateCategoryDto categoryToCreateDto(Category category) {
        return CreateCategoryDto.builder()
                .id(category.getId())
                .transactionType(category.getTransactionType())
                .categoryName(category.getName())
                .build();
    }

    public UserDto userToDto(User user) {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bankCards(user.getBankCards())
                .expenses(user.getBankCards().stream()
                        .flatMap(bankCard -> bankCard.getTransactions().stream())
                        .filter(transaction -> transaction.getCategory().getTransactionType().equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .incomes(user.getBankCards().stream()
                        .flatMap(bankCard -> bankCard.getTransactions().stream())
                        .filter(transaction -> transaction.getCategory().getTransactionType().equals(TransactionType.INCOME))
                        .collect(Collectors.toList()))
                .role(String.valueOf(user.getRole()))
                .status(String.valueOf(user.getStatus()))
                .subscription(user.getSubscription().getSubscriptionStatus().toString())
                .subscriptionPrice(user.getSubscription().getPrice())
                .subscriptionFinishDate(user.getSubscriptionFinishDate())
                .build();
        if (user.getAvatar() != null) {
            dto.setAvatar(imageConvertor.convertByteImageToString(user.getAvatar().getImageBytes()));
        }
        return dto;
    }

    public UserUpdateDto clientToClientUpdateDto(User user) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        Image image = user.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userUpdateDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userUpdateDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userUpdateDto.toBuilder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        Image image = user.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userDto.toBuilder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole().toString())
                .status(String.valueOf(user.getStatus()))
                .build();
    }

    public BankTransaction transactionDtoToTransaction(CreateTransactionDto transactionDto, Currency currency) {
        return BankTransaction.builder()
                .name(transactionDto.getTransactionName())
                .time(LocalDateTime.parse(transactionDto.getTime()))
                .sum((int) Math.round(transactionDto.getSum() * currency.getDelimiter()))
                .currency(currency)
                .build();
    }

    public Category categoryDtoToCategory(CreateCategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getCategoryName())
                .transactionType(categoryDto.getTransactionType())
                .build();
    }

    public PaymentCreateRequest subscriptionToPaymentRequest(Subscription subscription) {
        return PaymentCreateRequest.builder()
                .price((long) subscription.getPrice())
                .subscriptionPlan(subscription.getSubscriptionStatus().name())
                .build();
    }

    public BankCard bankCardDtoToBankCard(BankCardDto bankCardDto, Currency currency) {
        return BankCard.builder()
                .id(bankCardDto.getId())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .cardHolder(bankCardDto.getCardHolder())
                .currency(currency)
                .balance((int) Math.round((bankCardDto.getBalance()) * currency.getDelimiter()))
                .build();
    }

    public BankCardDto bankCardToBankCardDto(BankCard bankCard) {
        return BankCardDto.builder()
                .id(bankCard.getId())
                .bankName(bankCard.getBankName())
                .cardNumber(bankCard.getCardNumber())
                .cardHolder(bankCard.getCardHolder())
                .currencyShortName(bankCard.getCurrency().getShortName())
                .delimiter(bankCard.getCurrency().getDelimiter())
                .balance(bankCard.getBalance())
                .build();
    }

    public MonobankAccount monobankAccountDtoToMonobankAccount(MonobankAccountDto monobankAccountDto, Currency currency, MonobankIntegration monobankIntegration) {
        return MonobankAccount.builder()
                .monobankId(monobankAccountDto.getId())
                .type(monobankAccountDto.getType())
                .balance(monobankAccountDto.getBalance() - monobankAccountDto.getCreditLimit())
                .currency(currency)
                .maskedPan(monobankAccountDto.getMaskedPan().get(0))
                .holderName(monobankAccountDto.getHolderName())
                .creditLimit(monobankAccountDto.getCreditLimit())
                .monobankId(monobankAccountDto.getId())
                .monobankIntegration(monobankIntegration)
                .build();
    }

    public MonobankAccountDto monobankAccountToMonobankAccountDto(MonobankAccount monobankAccount) {
        return MonobankAccountDto.builder()
                .type(monobankAccount.getType())
                .balance(monobankAccount.getBalance())
                .currency(monobankAccount.getCurrency().getShortName())
                .maskedPan(Collections.singletonList(monobankAccount.getMaskedPan()))
                .currencyCode(monobankAccount.getCurrency().getCode())
                .holderName(monobankAccount.getHolderName())
                .currencyDelimiter(monobankAccount.getCurrency().getDelimiter())
                .creditLimit(monobankAccount.getCreditLimit())
                .build();
    }

    public BankCard monobankAccountToBankCard(MonobankAccount monobankAccount, User user) {
        String bankName = "monobank(" + monobankAccount.getType();
        if (monobankAccount.getCurrency().getCode() != HRYVNIA_CURRENCY_CODE) {
            bankName += ", " + monobankAccount.getCurrency().getShortName();
        }
        bankName += ")";
        return BankCard.builder()
                .bankName(bankName)
                .currency(monobankAccount.getCurrency())
                .balance(monobankAccount.getBalance())
                .cardNumber(monobankAccount.getMaskedPan())
                .cardHolder(monobankAccount.getHolderName())
                .user(user)
                .build();
    }

    public Transaction MonobankAccountTransactionDto(MonobankAccountTransactionDto monobankAccountTransactionDto, BankCard bankCard, BankTransaction bankTransaction, Category category) {
        return Transaction.builder()
                .bankCard(bankCard)
                .bankTransaction(bankTransaction)
                .category(category)
                .build();
    }

    public DashboardBankCardDto bankCardToDashboardBankCardDto(BankCard bankCard) {
        return DashboardBankCardDto.builder()
                .id(bankCard.getId())
                .holderName(bankCard.getCardHolder())
                .balance((double) bankCard.getBalance() / bankCard.getCurrency().getDelimiter())
                .cardNumber(String.valueOf(bankCard.getCardNumber()))
                .currencyShortName(bankCard.getCurrency().getShortName())
                .bankName(bankCard.getBankName())
                .currency(bankCard.getCurrency().getShortName())
                .currencyCode(bankCard.getCurrency().getCode())
                .build();
    }

    public MonobankTransaction monobankAccountTransactionDtoToMonobankTransaction(MonobankAccountTransactionDto monobankAccountTransactionDto) {
        return MonobankTransaction.builder()
                .monobankId(monobankAccountTransactionDto.getId())
                .build();
    }
}
