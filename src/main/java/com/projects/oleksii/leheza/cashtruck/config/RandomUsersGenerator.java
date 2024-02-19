package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.repository.CustomUserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Configuration;

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

    private final ClientService clientService;
    private final ManagerService managerService;
    private final SavingService savingService;
    private final BankCardService bankCardService;
    private final TransactionService transactionService;
    private final BankTransactionService bankTransactionService;
    private final CategoryService categoryService;
    private final CustomUserRepository customUserRepository;

    private final Random random;
    private final Faker faker;

    public void generateRandomClientFields(int clientsNumber, int managersNumber, int adminsNumber, int bankCardsNumber, int transactionNumber) {
//    prepare for client generation
        int allUsers = clientsNumber + managersNumber + adminsNumber;
        generateBankCards(bankCardsNumber);
        generateRandomUsers(allUsers);
        generateRandomSavings(clientsNumber);
        generateRandomBankTransactions(transactionNumber);
        List<BankTransaction> allBankTransactions = bankTransactionService.findAll();
        List<BankTransaction> firstBankTransactions = allBankTransactions.subList(1, allBankTransactions.size() / 2);
        List<BankTransaction> lastBankTransactions = allBankTransactions.subList(allBankTransactions.size() / 2, allBankTransactions.size());
        generateRandomTransactionIncomes(firstBankTransactions);
        generateRandomTransactionExpenses(lastBankTransactions);
//
        generateRandomClients(clientsNumber);
        generateRandomManagers(managersNumber);
        generateRandomAdmins(adminsNumber);
    }

    private void generateRandomUsers(int userNumber) {
        List<String> firstNames = Stream.generate(() -> faker.name().firstName()).distinct().limit(userNumber).toList();
        List<String> lastNames = Stream.generate(() -> faker.name().lastName()).distinct().limit(userNumber).toList();
        IntStream.range(1, userNumber).forEach(index -> {
            CustomUser customUser = CustomUser.builder()
                    .firstName(firstNames.get(index - 1))
                    .lastName(lastNames.get(index - 1))
                    .email(faker.internet().emailAddress())
                    .password(faker.lorem().sentence(2))
                    .build();
            customUserRepository.save(customUser);
        });
    }

    private void generateRandomClients(int clientsNumber) {
        List<Saving> savings = savingService.findAll();
        List<Transaction> allIncomes = transactionService.findAllIncomeTransactions();
        List<Transaction> allExpenses = transactionService.findAllExpenseTransactions();
        List<CustomUser> customUsers = customUserRepository.findAll();
        int incomesPerClient = allIncomes.size() / clientsNumber;
        int expensesPerClient = allExpenses.size() / clientsNumber;
        IntStream.range(1, clientsNumber).forEach(index -> {
            List<Transaction> incomes = allIncomes.subList(index * (incomesPerClient) - incomesPerClient + 1, index * incomesPerClient);
            List<Transaction> expenses = allExpenses.subList(index * (expensesPerClient) - expensesPerClient + 1, index * expensesPerClient);
            List<Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(incomes);
            allTransactions.addAll(expenses);
            CustomUser customUser = customUsers.get(index - 1);
            String firstName = customUser.getFirstName();
            String lastName = customUser.getLastName();
            String fullName = firstName + lastName;
            Saving saving = savings.get(index - 1);
            saveBankCardHolderName(fullName, saving);
            Client client = Client.builder()
                    .customUser(customUser)
                    .saving(saving)
                    .transactions(allTransactions)
                    .build();
            clientService.saveClient(client);
            savingService.assignBankCardsToClient((long) index, saving.getBankCards().stream().toList());
        });
    }

    private void generateRandomManagers(int managersNumber) {
        int clientsNumber = clientService.findAll().size();
        List<CustomUser> customUsers = customUserRepository.findAll();
        IntStream.range(clientsNumber, clientsNumber + managersNumber).forEach(index -> {
            Manager manager = Manager.builder()
                    .customUser(customUsers.get(index - 1))
                    .build();
            managerService.saveManager(manager);
        });
    }

    private void generateRandomAdmins(int adminNumber) {
        int clientsNumber = clientService.findAll().size();
        int managersNumber = managerService.findAllManagers().size();
        List<CustomUser> customUsers = customUserRepository.findAll();
        IntStream.range(clientsNumber + managersNumber, clientsNumber + managersNumber + adminNumber).forEach(index -> {
            Manager manager = Manager.builder()
                    .customUser(customUsers.get(index - 1))
                    .build();
            managerService.saveManager(manager);
        });
    }

    private void generateRandomSavings(int savingNumber) {
        List<BankCard> bankCards = bankCardService.findAll();
        List<Saving> savings = IntStream.range(1, savingNumber)
                .mapToObj(index -> {
                    Set<BankCard> selectedBankCards = getRandomCards(bankCards, 1, 3);
                    return Saving.builder()
                            .bankCards(selectedBankCards)
                            .build();
                })
                .toList();
        savingService.saveAll(savings);
    }

    private Set<BankCard> getRandomCards(List<BankCard> bankCards, int minSize, int maxSize) {
        int clientBankCards = random.nextInt(maxSize - minSize + 1) + minSize;
        int randomCardNumber = random.nextInt(bankCards.size() - maxSize);
        return new HashSet<>(bankCards.subList(randomCardNumber, randomCardNumber + Math.min(clientBankCards, bankCards.size())));
    }

    private void generateBankCards(Integer bankCardsNumber) {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2032, 1, 1);
        IntStream.range(1, bankCardsNumber + 1).mapToObj(index -> BankCard.builder()
                .balance(new BigDecimal(random.nextDouble(5000) + 5000))
                .bankName(faker.company().name())
                .cardNumber(faker.number().digits(16))
                .expiringDate(faker.date().between(
                        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                .cvv(Integer.toString(random.nextInt(900) + 100))
                .build()).forEach(bankCardService::save);
    }

    private void generateRandomBankTransactions(int transactionsNumber) {
        List<BankCard> allBankCards = bankCardService.findAll();
        List<BankCard> bankCardsFrom = allBankCards.subList(0, allBankCards.size() / 2);
        List<BankCard> bankCardsTo = allBankCards.subList(allBankCards.size() / 2, allBankCards.size());
        LocalDateTime now = LocalDateTime.now();
        List<BankTransaction> bankTransactions = IntStream.range(1, transactionsNumber)
                .mapToObj(index ->
                        BankTransaction.builder()
                                .sum(new BigDecimal(random.nextDouble() * 1000 + 50))
                                .from(bankCardsFrom.get(random.nextInt(bankCardsFrom.size())))
                                .to(bankCardsTo.get(random.nextInt(bankCardsTo.size())))
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

    private void saveBankCardHolderName(String fullName, Saving saving) {
        saving.getBankCards().forEach(bankCard -> {
            bankCard.setNameOnCard(fullName);
            bankCardService.save(bankCard);
        });
    }
}
