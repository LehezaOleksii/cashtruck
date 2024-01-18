package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.DtoConvertor;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.ExpensesRepository;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final IncomeRepository incomeRepository;
    private final ExpensesRepository expensesRepository;
    private final DtoConvertor dtoConvertor;
//    private final PasswordEncoder passwordEncoder;


//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Client client = clientRepository.findByEmail(email);
//        if (client == null) {
//            throw new UsernameNotFoundException("User with email " + email + " not found");
//        }
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(client.getRole().name()));
//        return new User(client.getEmail(), client.getPassword(), authorities);
//    }

    @Override
    public void saveClient(CreateClientDto createClientDto) throws IllegalArgumentException {
        if (!Optional.ofNullable(createClientDto).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
        if (existByEmail(createClientDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
//        client.setPassword(passwordEncoder.encode(client.getPassword()));
        Client client = new Client();
        client.toBuilder()
                .firstname(createClientDto.getFirstname())
                .lastname(createClientDto.getLastname())
                .email(createClientDto.getEmail())
                .password(createClientDto.getPassword())
                .role(UserRole.Client).build();
        clientRepository.save(client);
    }

    @Override
    public void saveClient(Client client) throws IllegalArgumentException {
        if (!Optional.ofNullable(client).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
        if (existByEmail(client.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        clientRepository.save(client);
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void deleteById(Long clientId) throws IllegalStateException {
        boolean exists = clientRepository.existsById(clientId);
        if (!exists) {
            throw new IllegalStateException("Client with" + clientId + "does not exists");
        }
        clientRepository.deleteById(clientId);
    }

    @Override
    public void updateClientInfo(Long clientId, CreateClientDto createClientDto) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = createClientDto.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentClient.toBuilder().firstname(createClientDto.getFirstname())
                .lastname(createClientDto.getLastname()).build();
        clientRepository.save(currentClient);
    }

    @Override
    @Transactional
    public void updateClient(Long clientId, Client client) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = client.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentClient.toBuilder().firstname(client.getFirstname())
                .lastname(client.getLastname()).incomes(client.getIncomes()).expenses(client.getExpenses())
                .email(updatedEmail).build();
        clientRepository.save(currentClient);
    }

    //For UI
    @Override
    public ClientStatisticDto getClientStatisticByClientId(Long clientId) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (!optionalClient.isPresent()) {
            return new ClientStatisticDto();
        }
        return createStatisticDto(optionalClient.get());
    }

    @Override//TODO use only one method (update) se tranisction
    public void addIncome(Long clientId, Income income) {
        Optional.ofNullable(income)
                .orElseThrow(() -> new IllegalArgumentException("Income cannot be null"));
        Optional.ofNullable(income.getTransaction())
                .orElseThrow(() -> new IllegalArgumentException("Income transaction cannot be null"));
        incomeRepository.save(income);
        Client client = clientRepository.findById(clientId).get();
        List<Income> incomes = client.getIncomes();
        incomes.add(income);
        client.setIncomes(incomes);
        clientRepository.save(client);
    }

    @Override//TODO use only one method (update) se tranisction
    public void addExpense(Long clientId, Expense expense) {
        Optional.ofNullable(expense)
                .orElseThrow(() -> new IllegalArgumentException("Expense cannot be null"));
        Optional.ofNullable(expense.getTransaction())
                .orElseThrow(() -> new IllegalArgumentException("Expense transaction cannot be null"));
        expensesRepository.save(expense);
        Client client = clientRepository.findById(clientId).get();
        List<Expense> expenses = client.getExpenses();
        expenses.add(expense);
        client.setExpenses(expenses);
        clientRepository.save(client);
    }

    @Override
    public List<BankCard> getBankCardsByClientId(Long clientId) {
        if (clientRepository.findById(clientId).isPresent()) {
            return clientRepository.findById(clientId).get().getSaving().getBankCards().stream().toList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ClientDto getClient(Long clientId) {
        Client client = clientRepository.getReferenceById(clientId);
        return ClientDto.builder()
                .id(clientId)
                .saving(client.getSaving())
                .expenses(client.getExpenses().stream().map(dtoConvertor::expenseToDto).toList())
                .incomes(client.getIncomes().stream().map(dtoConvertor::incomeToDto).toList())
                .email(client.getEmail())
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .build();
    }

    private ClientStatisticDto createStatisticDto(Client client) {
        ClientStatisticDto clientStatisticDto = new ClientStatisticDto();
        Long clientId = client.getId();
        LocalDateTime endDate = LocalDateTime.now();
        setLastYearTransactions(clientId, endDate, clientStatisticDto);
        setLastMonthTransactions(clientId, endDate, clientStatisticDto);
        setLastWeekTransactions(clientId, endDate, clientStatisticDto);
        setTotalIncomeSum(clientId, endDate, clientStatisticDto);
        setTotalExpenseSum(clientId, endDate, clientStatisticDto);
        clientStatisticDto.setTotalBalance(getTotalBalance(client));
        return clientStatisticDto;
    }

    private void setTotalIncomeSum(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime tenYearsStartDate = endDate.minusYears(10);
        List<Income> lastTenYearsIncome = incomeRepository.findIncomesForPeriod(clientId, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalIncomeSum(getAllIncomeSum(lastTenYearsIncome));
    }

    private void setTotalExpenseSum(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime tenYearsStartDate = endDate.minusYears(10);
        List<Expense> lastTenYearsExpense = expensesRepository.findExpensesForPeriod(clientId, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalExpenseSum(getAllExpenseSum(lastTenYearsExpense));
    }

    private BigDecimal getTotalBalance(Client client) {
        if (client == null || client.getSaving() == null || client.getSaving().getBankCards() == null) {
            return BigDecimal.ZERO;
        }
        return client.getSaving().getBankCards().stream()
                .map(BankCard::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setLastYearTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneYearStartDate = endDate.minusYears(1);
        List<Expense> lastYearExpenses = expensesRepository.findExpensesForPeriod(clientId, oneYearStartDate, endDate);
        clientStatisticDto.setExpenses(lastYearExpenses);
        clientStatisticDto.setLastYearExpense(getAllExpenseSum(lastYearExpenses));
        List<Income> lastYearIncomes = incomeRepository.findIncomesForPeriod(clientId, oneYearStartDate, endDate);
        clientStatisticDto.setIncomes(lastYearIncomes);
        clientStatisticDto.setLastYearIncome(getAllIncomeSum(lastYearIncomes));
    }

    private void setLastMonthTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneMonthStartDate = endDate.minusMonths(1);
        List<Expense> lastMonthExpenses = expensesRepository.findExpensesForPeriod(clientId, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthExpense(getAllExpenseSum(lastMonthExpenses));
        List<Income> lastMonthIncomes = incomeRepository.findIncomesForPeriod(clientId, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthIncome(getAllIncomeSum(lastMonthIncomes));
    }

    private void setLastWeekTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneWeekStartDate = endDate.minusWeeks(1);
        List<Expense> lastWeekExpenses = expensesRepository.findExpensesForPeriod(clientId, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekExpense(getAllExpenseSum(lastWeekExpenses));
        List<Income> lastWeekIncomes = incomeRepository.findIncomesForPeriod(clientId, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekIncome(getAllIncomeSum(lastWeekIncomes));
    }

    private BigDecimal getAllExpenseSum(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getTransaction)
                .map(Transaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAllIncomeSum(List<Income> incomes) {
        return incomes.stream()
                .map(Income::getTransaction)
                .map(Transaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean existByEmail(String email) {
        return clientRepository.findByEmail(email) != null;
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
