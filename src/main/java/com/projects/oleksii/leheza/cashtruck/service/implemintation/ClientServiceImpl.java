package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ClientUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientHeaderDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;

    private final ClientRepository clientRepository;
    private final CustomUserRepository customUserRepository;
    private final DtoMapper dtoMapper;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final ImageConvertor imageConvertor;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

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
    public Client saveClient(CreateClientDto createClientDto) throws IllegalArgumentException {
        if (!Optional.ofNullable(createClientDto).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
        if (existByEmail(createClientDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
//        client.setPassword(passwordEncoder.encode(client.getPassword()));
        CustomUser customUser = CustomUser.builder()
                .firstName(createClientDto.getFirstName())
                .lastName(createClientDto.getLastName())
                .email(createClientDto.getEmail())
                .password(createClientDto.getPassword())
                .build();
        Client client = Client.builder()
                .customUser(customUser)
                .build();
        return clientRepository.save(client);
    }

    @Override
    public Client saveClient(Client client) throws IllegalArgumentException {
        if (!Optional.ofNullable(client).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
//        if (existByEmail(client.getCustomUser().getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
        return clientRepository.save(client);
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findClientByCustomUser_Email(email);
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
    public void updateClientInfo(Long clientId, ClientUpdateDto clientUpdateDto) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = clientUpdateDto.getEmail();
        String currentEmail = currentClient.getCustomUser().getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        CustomUser user = currentClient.getCustomUser().toBuilder()
                .phoneNumber(clientUpdateDto.getPhoneNumber())
                .country(clientUpdateDto.getCountry())
                .language(clientUpdateDto.getLanguage())
                .firstName(clientUpdateDto.getFirstName())
                .lastName(clientUpdateDto.getLastName())
                .email(clientUpdateDto.getEmail())
                .password(clientUpdateDto.getPassword())
                .avatar(imageRepository.save(new Image(imageConvertor.convertStringToByteImage(clientUpdateDto.getAvatar()))))
                .role(UserRole.Client)
                .build();
        customUserRepository.save(user);
    }

    @Override
    @Transactional
    public void updateClient(Long clientId, Client client) throws IllegalStateException {
//        Client currentClient = clientRepository.findById(clientId).get();
//        String updatedEmail = client.getEmail();
//        String currentEmail = currentClient.getEmail();
//        if (isEmailTaken(currentEmail, updatedEmail)) {
//            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
//        }
//        currentClient.toBuilder().firstname(client.getFirstname())
//                .lastname(client.getLastname()).transactions(client.getTransactions()).expenses(client.getExpenses())
//                .email(updatedEmail).build();
//        clientRepository.save(currentClient);
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

    @Override//TODO use only one method (update) save tranisction
    public void addTransaction(Long clientId, Transaction transaction) {
        Optional.ofNullable(transaction)
                .orElseThrow(() -> new IllegalArgumentException("Transaction cannot be null"));
        Optional.ofNullable(transaction.getBankTransaction())
                .orElseThrow(() -> new IllegalArgumentException("Transaction transaction cannot be null"));
        transactionRepository.save(transaction);
        Client client = clientRepository.findById(clientId).get();
        List<Transaction> transactions = client.getTransactions();
        transactions.add(transaction);
        client.setTransactions(transactions);
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
    public ClientDto getClientDto(Long clientId) {
        return dtoMapper.clientToDto(clientRepository.findById(clientId).get());
    }

    @Override
    public ClientUpdateDto getClientUpdateDto(Long clientId) {
        return dtoMapper.clientToClientUpdateDto(clientRepository.findById(clientId).get());
    }

    @Override
    public Client getClient(Long clientId) {
        return clientRepository.findById(clientId).get();
    }

    @Override
    @Transactional
    public ClientHeaderDto getHeaderClientData(Long clientId) {
        if (clientRepository.findAvatarByClientId(clientId)!= null && clientRepository.findAvatarByClientId(clientId).length > 0) {
            return ClientHeaderDto.builder()
                    .id(clientId)
                    .avatar(imageConvertor.convertByteImageToString(clientRepository.findAvatarByClientId(clientId)))
                    .build();
        } else {
            return ClientHeaderDto.builder()
                    .id(clientId)
                    .avatar(imageService.getDefaultAvatarImage())
                    .build();
        }
    }

    @Override
    public ClientDto getDtoClient(Long clientId) {
        Client client = clientRepository.getReferenceById(clientId);
        CustomUser customUser = customUserRepository.getReferenceById(clientId);
        return ClientDto.builder()
                .id(clientId)
                .saving(client.getSaving())
                .expenses(client.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType() == TransactionType.EXPENSE)
                        .toList())
                .incomes(client.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType() == TransactionType.INCOME)
                        .toList())
                .email(customUser.getEmail())
                .password(customUser.getPassword())
                .firstName(customUser.getFirstName())
                .lastName(customUser.getLastName())
                .avatar(customUser.getAvatar().getImageBytes())
                .build();
    }

    @Override
    public CreateClientDto getCreateClientDto(Long clientId) {
        return dtoMapper.clientToCreateDto(clientRepository.findById(clientId).get());
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
        List<Transaction> lastTenYearsIncome = bankTransactionRepository.findTransactionForPeriod(clientId, INCOME_TRANSACTION_TYPE, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalIncomeSum(getAllTransactionSum(lastTenYearsIncome));
    }

    private void setTotalExpenseSum(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime tenYearsStartDate = endDate.minusYears(10);
        List<Transaction> lastTenYearsExpense = bankTransactionRepository.findTransactionForPeriod(clientId, EXPENSE_TRANSACTION_TYPE, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalExpenseSum(getAllTransactionSum(lastTenYearsExpense));
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
        List<Transaction> lastYearExpenses = bankTransactionRepository.findTransactionForPeriod(clientId, EXPENSE_TRANSACTION_TYPE, oneYearStartDate, endDate);
        clientStatisticDto.setExpenses(lastYearExpenses);
        clientStatisticDto.setLastYearExpense(getAllTransactionSum(lastYearExpenses));
        List<Transaction> lastYearIncomes = bankTransactionRepository.findTransactionForPeriod(clientId, INCOME_TRANSACTION_TYPE, oneYearStartDate, endDate);
        clientStatisticDto.setIncomes(lastYearIncomes);
        clientStatisticDto.setLastYearIncome(getAllTransactionSum(lastYearIncomes));
    }

    private void setLastMonthTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneMonthStartDate = endDate.minusMonths(1);
        List<Transaction> lastMonthExpenses = bankTransactionRepository.findTransactionForPeriod(clientId, EXPENSE_TRANSACTION_TYPE, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthExpense(getAllTransactionSum(lastMonthExpenses));
        List<Transaction> lastMonthIncomes = bankTransactionRepository.findTransactionForPeriod(clientId, INCOME_TRANSACTION_TYPE, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthIncome(getAllTransactionSum(lastMonthIncomes));
    }

    private void setLastWeekTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneWeekStartDate = endDate.minusWeeks(1);
        List<Transaction> lastWeekExpenses = bankTransactionRepository.findTransactionForPeriod(clientId, EXPENSE_TRANSACTION_TYPE, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekExpense(getAllTransactionSum(lastWeekExpenses));
        List<Transaction> lastWeekIncomes = bankTransactionRepository.findTransactionForPeriod(clientId, INCOME_TRANSACTION_TYPE, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekIncome(getAllTransactionSum(lastWeekIncomes));
    }

    private BigDecimal getAllTransactionSum(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getBankTransaction)
                .map(BankTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean existByEmail(String email) {
        return clientRepository.findClientByCustomUser_Email(email) != null;
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
