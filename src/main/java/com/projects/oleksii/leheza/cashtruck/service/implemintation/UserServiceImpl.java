package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.beans.Transient;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final ImageConvertor imageConvertor;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final DtoMapper dtoMapper;

    @Override
    public User saveClient(CreateUserDto createUserDto) throws IllegalArgumentException {
        if (!Optional.ofNullable(createUserDto).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
        if (existByEmail(createUserDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
//        client.setPassword(passwordEncoder.encode(client.getPassword()));
        User user = User.builder()
                .firstName(createUserDto.getFirstName())
                .lastName(createUserDto.getLastName())
                .email(createUserDto.getEmail())
                .password(createUserDto.getPassword())
                .status(ActiveStatus.INACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        if (!Optional.ofNullable(user).isPresent()) {
            throw new IllegalStateException("Client is empty");
        }
//        if (existByEmail(client.getCustomUser().getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
        return userRepository.save(user);
    }

    @Override
    public Boolean verifyEmailToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        if (user == null) {
            throw new RuntimeException();
        }
        user.setStatus(ActiveStatus.ACTIVE);
        userRepository.save(user);
        confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllManagers() {
        return null;
    }

    @Override
    public List<User> findAllAdmins() {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void updateUserInfo(Long userId, UserUpdateDto userUpdateDto) {
        User currentUser = userRepository.findById(userId).get();
        String updatedEmail = userUpdateDto.getEmail();
        String currentEmail = currentUser.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentUser = currentUser.toBuilder()
                .firstName(userUpdateDto.getFirstName())
                .lastName(userUpdateDto.getLastName())
                .password(userUpdateDto.getPassword())
                .country(userUpdateDto.getCountry())
                .phoneNumber(userUpdateDto.getPhoneNumber())
                .language(userUpdateDto.getLanguage())
                .email(updatedEmail).build();
        userRepository.save(currentUser);
    }

    @Override
    public void updateClient(Long clientId, UserUpdateDto userDto) {
        User currentClient = userRepository.findById(clientId).get();
        String updatedEmail = userDto.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        User user = currentClient.toBuilder()
                .phoneNumber(userDto.getPhoneNumber())
                .country(userDto.getCountry())
                .language(userDto.getLanguage())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .avatar(imageRepository.save(new Image(imageConvertor.convertStringToByteImage(userDto.getAvatar()))))
//                .role(UserRole.Client)
                .build();
        userRepository.save(user);
    }

    @Override
    public UserUpdateDto getClientUpdateDto(Long clientId) {
        return dtoMapper.clientToClientUpdateDto(userRepository.findById(clientId).get());
    }

    @Override
    public void updateToManager(Long userId) {
    }

    @Override
    public UserDto getUserDto(Long userId) {
        return dtoMapper.userToDto(userRepository.findById(userId).get());
    }

    @Override
    public CreateUserDto getCreateUserDto(Long userId) {
        return dtoMapper.clientToCreateDto(userRepository.findById(userId).get());
    }

    @Override
    public ClientStatisticDto getClientStatisticByUserId(Long userId) {
        Optional<User> optionalClient = userRepository.findById(userId);
        return optionalClient.map(this::createStatisticDto).orElseGet(ClientStatisticDto::new);
    }

    @Override//TODO use only one method (update) save tranisction
    public void addTransaction(Long clientId, Transaction transaction) {
        Optional.ofNullable(transaction)
                .orElseThrow(() -> new IllegalArgumentException("Transaction cannot be null"));
        Optional.ofNullable(transaction.getBankTransaction())
                .orElseThrow(() -> new IllegalArgumentException("Transaction transaction cannot be null"));
        transactionRepository.save(transaction);
        User client = userRepository.findById(clientId).get();
        List<Transaction> transactions = client.getTransactions();
        transactions.add(transaction);
        client.setTransactions(transactions);
        userRepository.save(client);
    }

    @Override
    public List<BankCard> getBankCardsByUserId(Long clientId) {
        if (userRepository.findById(clientId).isPresent()) {
            return userRepository.findById(clientId).get().getSaving().getBankCards().stream().toList();
        } else {
            return new ArrayList<>();
        }
    }


    @Override
    @Transactional
    public UserHeaderDto getHeaderClientData(Long userId) {
        byte[] avatar = userRepository.findAvatarByUserId(userId);
        if (avatar != null && avatar.length > 0) {
            return UserHeaderDto.builder()
                    .id(userId)
                    .avatar(imageConvertor.convertByteImageToString(userRepository.findAvatarByUserId(userId)))
                    .build();
        } else {
            return UserHeaderDto.builder()
                    .id(userId)
                    .avatar(imageService.getDefaultAvatarImage())
                    .build();
        }
    }

    @Override
    @Transient
    public void updateAvatar(Long userId, MultipartFile avatar) {
        User user = userRepository.findById(userId).get();
        try {
            Image image = new Image(avatar.getBytes());
            imageRepository.save(image);
            user.setAvatar(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.save(user);
    }

    private ClientStatisticDto createStatisticDto(User client) {
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

    private BigDecimal getTotalBalance(User client) {
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
        return email.equals(userRepository.findByEmailIgnoreCase(email).getEmail());
//        return adminRepository.findAdminByUser_Email(email).isPresent();
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}

//    @Override
//    public UserDto findAll() {
//        return userRepository.findAll().stream()
//                .map(dtoMapper::userToUserDto)
//                .toList();
//    }
//
//    @Override
//    public Boolean verifyToken(String token) {
//        Confirmation confirmation = confirmationRepository.findByToken(token);
//        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
//        if (user == null) {
//            throw new RuntimeException();
//        }
//        user.setEnable(true);
//        userRepository.save(user);
//        confirmationRepository.delete(confirmation);
//        return Boolean.TRUE;
//    }
//
//    /////////////////
//    //CLIENT
//

//
////    private final PasswordEncoder passwordEncoder;
//
//
////    @Override
////    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
////        Client client = clientRepository.findByEmail(email);
////        if (client == null) {
////            throw new UsernameNotFoundException("User with email " + email + " not found");
////        }
////        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
////        authorities.add(new SimpleGrantedAuthority(client.getRole().name()));
////        return new User(client.getEmail(), client.getPassword(), authorities);
////    }
//
//    @Override
//    public User saveClient(CreateClientDto createClientDto) throws IllegalArgumentException {
//        if (!Optional.ofNullable(createClientDto).isPresent()) {
//            throw new IllegalStateException("Client is empty");
//        }
//        if (existByEmail(createClientDto.getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
////        client.setPassword(passwordEncoder.encode(client.getPassword()));
//        User user = User.builder()
//                .firstName(createClientDto.getFirstName())
//                .lastName(createClientDto.getLastName())
//                .email(createClientDto.getEmail())
//                .password(createClientDto.getPassword())
//                .build();
//        return clientRepository.save(user);
//    }
//
//    @Override
//    public User saveClient(User user) throws IllegalArgumentException {
//        if (!Optional.ofNullable(user).isPresent()) {
//            throw new IllegalStateException("Client is empty");
//        }
////        if (existByEmail(client.getCustomUser().getEmail())) {
////            throw new IllegalStateException("Email taken");
////        }
//        return clientRepository.save(user);
//    }
//
//    @Override
//    public User findByEmail(String email) {
//        return userRepository.findClientByUser_Email(email);
//    }
//
//
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public void deleteById(Long clientId) throws IllegalStateException {
//        boolean exists = clientRepository.existsById(clientId);
//        if (!exists) {
//            throw new IllegalStateException("Client with" + clientId + "does not exists");
//        }
//        clientRepository.deleteById(clientId);
//    }
//
//    @Override
//    public void updateClientInfo(Long clientId, UserUpdateDto userUpdateDto) throws IllegalStateException {
//        User currentClient = clientRepository.findById(clientId).get();
//        String updatedEmail = userUpdateDto.getEmail();
//        String currentEmail = currentClient.getUser().getEmail();
//        if (isEmailTaken(currentEmail, updatedEmail)) {
//            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
//        }
//        User user = currentClient.getUser().toBuilder()
//                .phoneNumber(userUpdateDto.getPhoneNumber())
//                .country(userUpdateDto.getCountry())
//                .language(userUpdateDto.getLanguage())
//                .firstName(userUpdateDto.getFirstName())
//                .lastName(userUpdateDto.getLastName())
//                .email(userUpdateDto.getEmail())
//                .password(userUpdateDto.getPassword())
//                .avatar(imageRepository.save(new Image(imageConvertor.convertStringToByteImage(userUpdateDto.getAvatar()))))
////                .role(UserRole.Client)
//                .build();
//        userRepository.save(user);
//    }
//
//    @Override
//    @Transactional
//    public void updateClient(Long clientId, User client) throws IllegalStateException {
////        Client currentClient = clientRepository.findById(clientId).get();
////        String updatedEmail = client.getEmail();
////        String currentEmail = currentClient.getEmail();
////        if (isEmailTaken(currentEmail, updatedEmail)) {
////            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
////        }
////        currentClient.toBuilder().firstname(client.getFirstname())
////                .lastname(client.getLastname()).transactions(client.getTransactions()).expenses(client.getExpenses())
////                .email(updatedEmail).build();
////        clientRepository.save(currentClient);
//    }
//
//    //For UI
//    @Override
//    public ClientStatisticDto getClientStatisticByUserId(Long clientId) {
//        Optional<User> optionalClient = clientRepository.findById(clientId);
//        if (!optionalClient.isPresent()) {
//            return new ClientStatisticDto();
//        }
//        return createStatisticDto(optionalClient.get());
//    }
//
//    @Override//TODO use only one method (update) save tranisction
//    public void addTransaction(Long clientId, Transaction transaction) {
//        Optional.ofNullable(transaction)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction cannot be null"));
//        Optional.ofNullable(transaction.getBankTransaction())
//                .orElseThrow(() -> new IllegalArgumentException("Transaction transaction cannot be null"));
//        transactionRepository.save(transaction);
//        User client = clientRepository.findById(clientId).get();
//        List<Transaction> transactions = client.getTransactions();
//        transactions.add(transaction);
//        client.setTransactions(transactions);
//        clientRepository.save(client);
//    }
//
//    @Override
//    public List<BankCard> getBankCardsByUserId(Long clientId) {
//        if (clientRepository.findById(clientId).isPresent()) {
//            return clientRepository.findById(clientId).get().getSaving().getBankCards().stream().toList();
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    public UserDto getClientDto(Long userId) {
//        return dtoMapper.clientToDto(clientRepository.findById(userId).get());
//    }
//
//    @Override
//    public UserUpdateDto getClientUpdateDto(Long clientId) {
//        return dtoMapper.clientToClientUpdateDto(clientRepository.findById(clientId).get());
//    }
//
//    @Override
//    public User getClient(Long clientId) {
//        return clientRepository.findById(clientId).get();
//    }
//
//    @Override
//    @Transactional
//    public UserHeaderDto getHeaderClientData(Long userId) {
//        if (clientRepository.findAvatarByClientId(userId)!= null && clientRepository.findAvatarByClientId(userId).length > 0) {
//            return UserHeaderDto.builder()
//                    .id(userId)
//                    .avatar(imageConvertor.convertByteImageToString(clientRepository.findAvatarByClientId(userId)))
//                    .build();
//        } else {
//            return UserHeaderDto.builder()
//                    .id(userId)
//                    .avatar(imageService.getDefaultAvatarImage())
//                    .build();
//        }
//    }
//
//    @Override
//    public UserDto getDtoClient(Long clientId) {
//        User client = clientRepository.getReferenceById(clientId);
//        User user = userRepository.getReferenceById(clientId);
//        return UserDto.builder()
//                .id(clientId)
//                .saving(client.getSaving())
//                .expenses(client.getTransactions().stream()
//                        .filter(transaction -> transaction.getCategory().getTransactionType() == TransactionType.EXPENSE)
//                        .toList())
//                .incomes(client.getTransactions().stream()
//                        .filter(transaction -> transaction.getCategory().getTransactionType() == TransactionType.INCOME)
//                        .toList())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .avatar(user.getAvatar().getImageBytes())
//                .build();
//    }
//
//    @Override
//    public CreateClientDto getCreateClientDto(Long clientId) {
//        return dtoMapper.clientToCreateDto(clientRepository.findById(clientId).get());
//    }
//

//
//
//    /////////////////
//    //MANAGER
//
//
//    @Override
//    public void saveManager(CreateUserDto createUserDto) {
//        if (existByEmail(createUserDto.getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
//        User user = User.builder()
//                .firstName(createUserDto.getFirstname())
//                .lastName(createUserDto.getLastname())
//                .email(createUserDto.getEmail())
//                .password(createUserDto.getPassword())
//                .build();
//        Manager manager = Manager.builder()
//                .user(user)
//                .build();
//        managerRepository.save(manager);
//    }
//
//    @Override
//    public void saveManager(Manager manager) {
//        if (!Optional.ofNullable(manager).isPresent()) {
//            throw new IllegalStateException("Manager is empty");
//        }
//        if (existByEmail(manager.getUser().getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
//        managerRepository.save(manager);
//    }
//
//    @Override
//    public List<Manager> findAllManagers() {
//        return managerRepository.findAll();//TODO find all - me
//    }
//
//    @Override
//    public Manager findManagerByEmail(String email) throws IllegalStateException {
//        return managerRepository.findManagerByUser_Email(email);
////                .orElseThrow(() -> new IllegalStateException("Manager not found with email: " + email)
//    }
//
//    @Override
//    public Manager findManagerById(Long userId) throws IllegalStateException {
//        Optional<Manager> managerOptional = managerRepository.findById(userId);
//        if (!managerOptional.isPresent()) {
//            throw new IllegalStateException("Manager with id " + userId + "doesn`t exist");
//        }
//        return managerOptional.get();
//    }
//
//    @Override
//    public void updateManagerInfo(Long managerId, ManagerUpdateDto managerUpdateDto) throws IllegalStateException {
//        Manager currentManager = managerRepository.findById(managerId).get();
//        String updatedEmail = managerUpdateDto.getEmail();
//        String currentEmail = currentManager.getUser().getEmail();
//        if (isEmailTaken(currentEmail, updatedEmail)) {
//            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
//        }
//        User user = currentManager.getUser().toBuilder()
//                .phoneNumber(managerUpdateDto.getPhoneNumber())
//                .country(managerUpdateDto.getCountry())
//                .language(managerUpdateDto.getLanguage())
//                .firstName(managerUpdateDto.getFirstName())
//                .lastName(managerUpdateDto.getLastName())
//                .email(managerUpdateDto.getEmail())
//                .password(managerUpdateDto.getPassword())
//                .avatar(imageRepository.save(new Image(imageConvertor.convertStringToByteImage(managerUpdateDto.getAvatar()))))
//                .role(UserRole.Client)
//                .build();
//        userRepository.save(user);
//    }
//
//    @Override
//    public void deleteManagerById(Long id) throws IllegalStateException {
//        Optional<Manager> manager = managerRepository.findById(id);
//        if (manager.isPresent()) {
//            managerRepository.deleteById(id);
//        } else {
//            throw new IllegalStateException("Manager with ID " + id + " doesn't exist");
//        }
//    }
//
//    private boolean existByEmail(String email) {
//        return managerRepository.findManagerByUser_Email(email) != null && email.equals(managerRepository.findManagerByUser_Email(email).getUser().getEmail());
////        managerRepository.findManagerByUser_Email(email).isPresent()
//    }
//
//    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
//        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
//    }
//
//
//    /////////////////
//    //ADMIN
//    @Override
//    public void saveAdmin(CreateAdminDto createAdminDto) throws IllegalStateException {
//        if (existByEmail(createAdminDto.getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
//        User user = User.builder()
//                .firstName(createAdminDto.getFirstname())
//                .lastName(createAdminDto.getLastname())
//                .password(createAdminDto.getPassword())
//                .email(createAdminDto.getEmail())
//                .build();
//        Admin admin = Admin.builder()
//                .user(user)
//                .build();
//        adminRepository.save(admin);
//    }
//
//    @Override
//    public void saveAdmin(Admin admin) {
//        if (!Optional.ofNullable(admin).isPresent()) {
//            throw new IllegalStateException("Admin is empty");
//        }
//        if (existByEmail(admin.getUser().getEmail())) {
//            throw new IllegalStateException("Email taken");
//        }
//        adminRepository.save(admin);
//    }
//
//    @Override
//    public List<Admin> findAllAdmins() {
//        return adminRepository.findAll();
//    }
//
//    @Override
//    public Admin findAdminByEmail(String email) {
//        return adminRepository.findAdminByUser_Email(email);
//    }
//
//    @Override
//    public void updateAdminInfo(Long id, CreateAdminDto createAdminDto) {
//        Admin currentAdmin = adminRepository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Admin with id " + id + " not found"));
//        String updatedEmail = createAdminDto.getEmail();
//        String currentEmail = currentAdmin.getUser().getEmail();
//        User currentUser = currentAdmin.getUser();
//        if (isEmailTaken(currentEmail, updatedEmail)) {
//            throw new IllegalStateException("Admin with " + updatedEmail + " has already exist");
//        }
//        currentUser.toBuilder()
//                .firstName(createAdminDto.getFirstname())
//                .lastName(createAdminDto.getLastname()).build();
//        userRepository.save(currentUser);
//    }
//
//    @Override
//    public void createManager(CreateUserDto createUserDto) {
//        managerService.saveManager(createUserDto);
//    }
//
//    @Override
//    public List<Manager> findAllManagers() {
//        return managerService.findAllManagers();
//    }
//
////    @Override
////    public void updateManagerInfo(Long managerId, CreateManagerDto createManagerDto) throws IllegalStateException {
////        managerService.updateManagerInfo(managerId, createManagerDto);
////    }
//
//    @Override
//    public void deleteManagerById(Long managerId) throws IllegalStateException {
//        managerService.deleteManagerById(managerId);
//    }
//
//    @Override
//    public void deleteClientById(Long clientId) {
//        clientService.deleteById(clientId);
//    }
//
//    @Override
//    public void changeClientInfoById(Long clientId, CreateClientDto createClientDto) {
////        clientService.updateClientInfo(clientId, createClientDto);
//    }
//
//    @Override
//    public void changeManagerInfoById(Long managerId, ManagerUpdateDto managerUpdateDto) {
//        managerService.updateManagerInfo(managerId, managerUpdateDto);
//    }
//
//    private boolean existByEmail(String email) {
//        return email.equals(adminRepository.findAdminByUser_Email(email).getUser().getEmail());
////        return adminRepository.findAdminByUser_Email(email).isPresent();
//    }
//
//    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
//        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
//    }
//}
