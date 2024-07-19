package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.SubscriptionRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class RandomUsersGenerator {

    private final UserService userService;
    private final BankCardService bankCardService;
    private final TransactionService transactionService;
    private final BankTransactionService bankTransactionService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final Random random;
    private final Faker faker;
    private final PasswordEncoder passwordEncoder;

    public void generateRandomClientFields(int clientsNumber, int managersNumber, int adminsNumber, int bankCardsNumber, int transactionNumber) {
//    prepare for client generation
        int allUsers = clientsNumber + managersNumber + adminsNumber;
        generateBankCards(bankCardsNumber, allUsers);
        generateRandomBankTransactions(transactionNumber);
        List<BankTransaction> allBankTransactions = bankTransactionService.findAll();
        List<BankTransaction> firstBankTransactions = allBankTransactions.subList(1, allBankTransactions.size() / 2);
        List<BankTransaction> lastBankTransactions = allBankTransactions.subList(allBankTransactions.size() / 2, allBankTransactions.size());
        generateRandomTransactionIncomes(firstBankTransactions);
        generateRandomTransactionExpenses(lastBankTransactions);
        generateRandomUsers(allUsers, passwordEncoder);
        generateRandomClients(clientsNumber);
        generateRandomManagers(managersNumber, clientsNumber);
        generateRandomAdmins(adminsNumber, managersNumber, clientsNumber);
        generateTestAccount(passwordEncoder);
    }

    private void generateTestAccount(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setPassword(
                passwordEncoder.encode("password"));
        user.setEmail("oleksii.leheza@gmail.com");
        user.setRole(Role.ROLE_CLIENT);
        user.setStatus(ActiveStatus.ACTIVE);
        Subscription subscription = subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.FREE)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription status with name:" + SubscriptionStatus.FREE + " does not exist"));
        user.setSubscription(subscription);
        user.setSubscriptionFinishDate(new Date());
        userService.save(user);
    }

    private void generateRandomUsers(int allUsers, PasswordEncoder passwordEncoder) {
        List<String> firstNames = Stream.generate(() -> faker.name().firstName()).limit(allUsers).toList();
        List<String> lastNames = Stream.generate(() -> faker.name().lastName()).limit(allUsers).toList();
        List<BankCard> allBankCards = bankCardService.findAll();
        IntStream.range(1, allUsers).forEach(index -> {
            String firstName = firstNames.get(index - 1);
            String lastName = lastNames.get(index - 1);
            String fullName = firstName + lastName;
            List<BankCard> bankCards = allBankCards.subList(index, index + 1);
            saveBankCardHolderName(fullName, bankCards);
            User user = new User();
            LocalDate currentDate = LocalDate.now();
            LocalDate subscriptionFinishDate = currentDate.plusYears(100);
            Date subscriptionFinishDateLegacy = Date.from(subscriptionFinishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Subscription subscription = subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.FREE)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription status with name:" + SubscriptionStatus.FREE + " does not exist"));
            String password = passwordEncoder.encode("password");
            user = user.toBuilder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(faker.internet().emailAddress())
                    .password(password)
                    .role(Role.ROLE_CLIENT)
                    .status(ActiveStatus.ACTIVE)
                    .subscription(subscription)
                    .subscriptionFinishDate(subscriptionFinishDateLegacy)
                    .bankCards(new HashSet<>(bankCards))
                    .build();
            userService.save(user);
        });
    }

    private void generateRandomClients(int clientsNumber) {
        List<User> users = userRepository.findAll();
        IntStream.range(1, clientsNumber).forEach(index -> {
            User user = users.get(clientsNumber);
            user.setRole(Role.ROLE_CLIENT);
            userService.save(user);
        });
    }

    private void generateRandomManagers(int managersNumber, int clientsNumber) {
        List<User> users = userRepository.findAll();
        IntStream.range(clientsNumber, clientsNumber + managersNumber).forEach(index -> {
            User user = users.get(clientsNumber);
            user.setRole(Role.ROLE_MANAGER);
            userService.save(user);
        });
    }

    private void generateRandomAdmins(int adminNumber, int managersNumber, int clientsNumber) {
        List<User> users = userRepository.findAll();
        IntStream.range(clientsNumber + managersNumber, clientsNumber + managersNumber + adminNumber).forEach(index -> {
            User user = users.get(clientsNumber);
            user.setRole(Role.ROLE_ADMIN);
            userService.save(user);
        });
    }

    private void generateBankCards(Integer bankCardsNumber, int allUsers) {
        List<Transaction> allIncomes = transactionService.findAllIncomeTransactions();
        List<Transaction> allExpenses = transactionService.findAllExpenseTransactions();
        int incomesPerClient = allIncomes.size() / allUsers;
        int expensesPerClient = allExpenses.size() / allUsers;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2032, 1, 1);
        IntStream.range(0, bankCardsNumber).forEach(index -> {
            List<Transaction> incomes = allIncomes.subList(index * incomesPerClient, Math.min((index + 1) * incomesPerClient, allIncomes.size()));
            List<Transaction> expenses = allExpenses.subList(index * expensesPerClient, Math.min((index + 1) * expensesPerClient, allExpenses.size()));
            List<Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(incomes);
            allTransactions.addAll(expenses);
            BankCard bankCard = BankCard.builder()
                    .balance(BigDecimal.valueOf(random.nextDouble() * 5000 + 5000))
                    .bankName(faker.company().name())
                    .cardNumber(faker.number().digits(16))
                    .expiringDate(faker.date().between(
                            Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .cvv(Integer.toString(random.nextInt(900) + 100))
                    .transactions(new HashSet<>(allTransactions))
                    .build();
            bankCardService.save(bankCard);
            for (Transaction transaction : allTransactions) {
                transaction.setBankCard(bankCard);
                transactionService.save(transaction);
            }
        });
    }

    private void generateRandomBankTransactions(int transactionsNumber) {
        LocalDateTime now = LocalDateTime.now();
        List<BankTransaction> bankTransactions = IntStream.range(1, transactionsNumber)
                .mapToObj(index ->
                        BankTransaction.builder()
                                .sum(new BigDecimal(random.nextDouble() * 1000 + 50))
                                .name(faker.commerce().productName())
                                .time(now.minus(random.nextInt(60), ChronoUnit.DAYS))
                                .build()).toList();
        bankTransactionService.saveAll(bankTransactions);
    }


    private void generateRandomTransactionIncomes(List<BankTransaction> bankTransactions) {
        List<Category> incomeCategories = categoryService.findAllIncomeCategories();
        bankTransactions.stream()
                .skip(1)
                .map(transaction -> Transaction.builder()
                        .category(incomeCategories.get(random.nextInt(incomeCategories.size())))
                        .bankTransaction(transaction)
                        .build()
                )
                .forEach(transactionService::save);
    }

    private void generateRandomTransactionExpenses(List<BankTransaction> bankTransactions) {
        List<Category> expenseCategories = categoryService.findAllExpensesCategories();
        bankTransactions.stream()
                .skip(1)
                .map(transaction -> Transaction.builder()
                        .category(expenseCategories.get(random.nextInt(expenseCategories.size())))
                        .bankTransaction(transaction)
                        .build()
                )
                .forEach(transactionService::save);
    }

    private LocalDateTime generateRandomLocalDateTime() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        long randomSeconds = ThreadLocalRandom.current().nextLong(start.until(end, ChronoUnit.SECONDS));
        return start.plusSeconds(randomSeconds);
    }

    private void saveBankCardHolderName(String fullName, List<BankCard> bankCards) {
        bankCards.forEach(bankCard -> {
            bankCard.setCardHolder(fullName);
            bankCardService.save(bankCard);
        });
    }
}
