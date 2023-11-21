package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientStatisticDto;
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
    public void saveClient(ClientDto clientDto) throws  IllegalArgumentException{
        if(!Optional.ofNullable(clientDto).isPresent()){
            throw  new IllegalStateException("Client is empty");
        }
        if (existByEmail(clientDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
//        client.setPassword(passwordEncoder.encode(client.getPassword()));
        Client client = new Client();
        client.toBuilder()
                .firstname(clientDto.getFirstname())
                .lastname(clientDto.getLastname())
                .email(clientDto.getEmail())
                .password(clientDto.getPassword())
                .role(UserRole.Client).build();
        clientRepository.save(client);
    }

    @Override
    public void saveClient(Client client) throws IllegalArgumentException {
        if(!Optional.ofNullable(client).isPresent()){
          throw  new IllegalStateException("Client is empty");
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
    public void updateClientInfo(Long clientId, ClientDto clientDto) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = clientDto.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentClient.toBuilder().firstname(clientDto.getFirstname())
                .lastname(clientDto.getLastname()).build();
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
        List<Income> incomes =client.getIncomes();
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
        List<Expense> expenses =client.getExpenses();
        expenses.add(expense);
        client.setExpenses(expenses);
        clientRepository.save(client);
    }

    @Override
    public List<BankCard> getBankCardsByClientId(Long clientId) {
        if (clientRepository.findById(clientId).isPresent()){
            return clientRepository.findById(clientId).get().getSaving().getBankCards().stream().toList();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override
    public Client getClient(Long clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    private ClientStatisticDto createStatisticDto(Client client){
        ClientStatisticDto clientStatisticDto = new ClientStatisticDto();
        Long clientId = client.getId();
        LocalDateTime endDate = LocalDateTime.now();
        setLastYearTransactions(clientId,endDate,clientStatisticDto);
        setLastMonthTransactions(clientId,endDate,clientStatisticDto);
        setLastWeekTransactions(clientId,endDate,clientStatisticDto);
        clientStatisticDto.setTotalBalance(getTotalBalance(client));
        return  clientStatisticDto;
    }

    private BigDecimal getTotalBalance(Client client){
        return client.getSaving().getBankCards().stream()
                .map(BankCard::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void setLastYearTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto){
        LocalDateTime oneYearStartDate = endDate.minusYears(1);
        List<Expense> lastYearExpenses = expensesRepository.findExpensesForPeriod(clientId,oneYearStartDate,endDate);
        clientStatisticDto.setExpenses(lastYearExpenses);
        clientStatisticDto.setLastYearExpense(getAllExpenseSum(lastYearExpenses));
        List<Income> lastYearIncomes = clientRepository.findIncomesForPeriod(clientId,oneYearStartDate,endDate);
        clientStatisticDto.setIncomes(lastYearIncomes);
        clientStatisticDto.setLastYearIncome(getAllIncomeSum(lastYearIncomes));
    }

    private void setLastMonthTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto){
        LocalDateTime oneMonthStartDate = endDate.minusMonths(1);
        List<Expense> lastMonthExpenses = expensesRepository.findExpensesForPeriod(clientId,oneMonthStartDate,endDate);
        clientStatisticDto.setLastMonthExpense(getAllExpenseSum(lastMonthExpenses));
        List<Income> lastMonthIncomes = clientRepository.findIncomesForPeriod(clientId,oneMonthStartDate,endDate);
        clientStatisticDto.setLastMonthIncome(getAllIncomeSum(lastMonthIncomes));
    }

    private void setLastWeekTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto){
        LocalDateTime oneWeekStartDate = endDate.minusWeeks(1);
        List<Expense> lastWeekExpenses = expensesRepository.findExpensesForPeriod(clientId,oneWeekStartDate,endDate);
        clientStatisticDto.setLastWeekExpense(getAllExpenseSum(lastWeekExpenses));
        List<Income> lastWeekIncomes = clientRepository.findIncomesForPeriod(clientId,oneWeekStartDate,endDate);
        clientStatisticDto.setLastWeekIncome(getAllIncomeSum(lastWeekIncomes));
    }

    private BigDecimal getAllExpenseSum(List<Expense>expenses){
        return expenses.stream()
                .map(Expense::getTransaction)
                .map(Transaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAllIncomeSum(List<Income>incomes){
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
