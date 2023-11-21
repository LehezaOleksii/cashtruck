package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
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
    private final AdminService adminService;
    private final SavingService savingService;
    private final BankCardService bankCardService;
    private final TransactionService transactionService;
    private final IncomeCategoryService incomeCategoryService;
    private final IncomeService incomeService;
    private final ExpensesCategoryService expensesCategoryService;
    private final ExpenseService expenseService;

    private final Random random;
    private final Faker faker;

    public void generateRandomUsers(int clientsNumber,int bankCardsNumber,int managersNumber,int adminNumber,int transactionNumber){
//    prepare for client generation
        generateBankCards(bankCardsNumber);
        generateRandomSavings(clientsNumber);
        generateRandomTransactions(transactionNumber);
        List<Transaction> allTransactions = transactionService.findAll();
        List<Transaction> firstTransactions = allTransactions.subList(1,allTransactions.size()/2);
        List<Transaction> lastTransactions = allTransactions.subList(allTransactions.size()/2,allTransactions.size());
        generateRandomIncomes(firstTransactions);
        generateRandomExpenses(lastTransactions);
//
        generateRandomClients(clientsNumber);
        generateRandomManagers(managersNumber);
        generateRandomAdmins(adminNumber);
    }
    private void generateRandomClients(int clientsNumber) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(clientsNumber).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(clientsNumber).toList();
        List<Saving> savings = savingService.findAll();
        List<Income> allIncomes = incomeService.findAll();
        List<Expense> allExpenses = expenseService.findAll();
        int incomesPerClient = allIncomes.size()/clientsNumber;
        int expensesPerClient = allExpenses.size()/clientsNumber;
        IntStream.range(1, clientsNumber).forEach(index -> {
            List<Income> incomes = allIncomes.subList(index*(incomesPerClient)-incomesPerClient+1,index*incomesPerClient);
            List<Expense> expenses = allExpenses.subList(index*(expensesPerClient)-expensesPerClient+1,index*expensesPerClient);
            String firstname=firstnames.get(index - 1);
            String lastname = lastnames.get(index - 1);
            String fullName =  firstname+lastname;
            Saving saving = savings.get(index - 1);
            saveBankCardHolderName(fullName,saving);
            Client client = Client.builder()
                    .firstname(firstname)
                    .lastname(lastname)
                    .email(faker.internet().emailAddress())
                    .password(faker.lorem().sentence(2))
                    .saving(saving)
                    .incomes(incomes)
                    .expenses(expenses)
                    .role(UserRole.Client)
                    .build();
            clientService.saveClient(client);
            savingService.assignBankCardsToClient((long)index,saving.getBankCards().stream().toList());
        });
    }

    private void generateRandomManagers(int managersNumber) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(managersNumber).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(managersNumber).toList();
        IntStream.range(1, managersNumber).mapToObj(index -> Manager.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(UserRole.Manager).build()).forEach(managerService::saveManager);
    }

    private void generateRandomAdmins(int adminNumber) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(adminNumber).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(adminNumber).toList();
        IntStream.range(1, adminNumber).mapToObj(index -> Admin.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(UserRole.Admin).build()).forEach(adminService::saveAdmin);
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
    private Set<BankCard> getRandomCards(List<BankCard>bankCards,int minSize, int maxSize){
        int clientBankCards = random.nextInt(maxSize - minSize + 1) + minSize;
        int randomCardNumber = random.nextInt(bankCards.size()-maxSize);
        return new HashSet<>(bankCards.subList(randomCardNumber, randomCardNumber+Math.min(clientBankCards, bankCards.size())));
    }

    private void generateBankCards(Integer bankCardsNumber){
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2032, 1, 1);
        IntStream.range(1, bankCardsNumber+1).mapToObj(index -> BankCard.builder()
                .balance(new BigDecimal(random.nextDouble(5000)+5000))
                .bankName(faker.company().name())
                .cardNumber(faker.number().digits(16))
                .expiringDate(faker.date().between(
                        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                .cvv(Integer.toString(random.nextInt(900) + 100))
                .build()).forEach(bankCardService::save);
    }

    private void generateRandomTransactions(int transactionsNumber) {
        List<BankCard> allBankCards = bankCardService.findAll();
        List<BankCard> bankCardsFrom = allBankCards.subList(0, allBankCards.size() / 2);
        List<BankCard> bankCardsTo = allBankCards.subList(allBankCards.size() / 2, allBankCards.size());
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = IntStream.range(1, transactionsNumber)
                .mapToObj(index ->
                      Transaction.builder()
                            .sum(new BigDecimal(random.nextDouble() * 1000 + 50))
                            .from(bankCardsFrom.get(random.nextInt(bankCardsFrom.size())))
                            .to(bankCardsTo.get(random.nextInt(bankCardsTo.size())))
                            .time(now.minus(random.nextInt(60), ChronoUnit.DAYS))
                            .build()).toList();
        transactionService.saveAll(transactions);
    }


    private void generateRandomIncomes( List<Transaction> transactions ){
        List<IncomeCategory> incomeCategories = incomeCategoryService.findAll();
        transactions.stream()
                .skip(1)
                .map(transaction -> Income.builder()
                        .incomeCategory(incomeCategories.get(random.nextInt(incomeCategories.size())))
                        .transaction(transaction)
                        .build()
                )
                .forEach(incomeService::save);
    }

    private void generateRandomExpenses( List<Transaction> transactions ){
        List<ExpensesCategory> expenseCategories = expensesCategoryService.findAll();
        transactions.stream()
                .skip(1)
                .map(transaction -> Expense.builder()
                        .expenseCategory(expenseCategories.get(random.nextInt(expenseCategories.size())))
                        .transaction(transaction)
                        .build()
                )
                .forEach(expenseService::save);
    }

    private LocalDateTime generateRandomLocalDateTime(){
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
