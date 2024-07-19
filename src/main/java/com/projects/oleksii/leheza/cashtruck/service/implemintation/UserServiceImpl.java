package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ImageException;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceAlreadyExistException;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.exception.UserPlanException;
import com.projects.oleksii.leheza.cashtruck.filter.UserSpecification;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.EmailService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final TransactionType INCOME_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType EXPENSE_TRANSACTION_TYPE = TransactionType.EXPENSE;
    public static final String SORT_PROPERTY_FIRST_NAME = "firstName";

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final ImageConvertor imageConvertor;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;
    private final UserSpecification userSpecification;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final UserDetailsService userDetailsService;
    private final BankCardRepository bankCardRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User saveNewUser(LoginDto loginDto) {
        User user = createUserEntity(loginDto);
        userRepository.save(user);
        log.info("save user with email:{}", loginDto.getLogin());
        sendConformationEmail(loginDto, user);
        return user;
    }

    @Override
    public User saveNewUserWithActiveStatus(LoginDto loginDto) {
        User user = createUserEntity(loginDto);
        user.setStatus(ActiveStatus.ACTIVE);
        userRepository.save(user);
        log.info("save user with email:{}", loginDto.getLogin());
        return user;
    }

    @Override
    public Page<UserDto> findAll(int page, int size) {
        Sort sort = Sort.by("firstName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(dtoMapper::userToDto);
    }

    @Override
    public PageDto<UserDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UserDto> users = userRepository.findAll(pageable).stream()
                .map(dtoMapper::userToUserDto).toList();
        PageDto<UserDto> forumCategoryPageDto = PageDto.<UserDto>builder()
                .data(users)
                .page(pageNumber)
                .size(pageSize)
                .totalSize(users.size())
                .build();
        forumCategoryPageDto = forumCategoryPageDto.toBuilder()
                .totalPage((int) Math.ceil((double) users.size() / pageSize))
                .build();
        return forumCategoryPageDto;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return dtoMapper.userToDto(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with does not exist")));
    }

    @Override
    public UserDto updateUserInfo(Long userId, UserUpdateDto userUpdateDto) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id does not exist"));
        String updatedEmail = userUpdateDto.getEmail();
        String currentEmail = currentUser.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            log.warn("user with email already taken. email:{}", userUpdateDto.getEmail());
            throw new ResourceAlreadyExistException("Client with email: " + updatedEmail + " has already exist");
        }
        currentUser = currentUser.toBuilder()
                .firstName(userUpdateDto.getFirstName())
                .lastName(userUpdateDto.getLastName())
                .email(updatedEmail).build();
        return dtoMapper.userToDto(userRepository.save(currentUser));
    }

    @Override
    public UserUpdateDto getClientUpdateDto(Long clientId) {
        return dtoMapper.clientToClientUpdateDto(userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + clientId + " does not exist")));
    }

    @Override
    public UserDto getUserDto(Long userId) {
        return dtoMapper.userToDto(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist")));
    }

    @Override
    public ClientStatisticDto getClientStatisticByUserId(Long userId) {
        Optional<User> optionalClient = userRepository.findById(userId);
        return optionalClient.map(this::createStatisticDto).orElseGet(ClientStatisticDto::new);
    }

    @Override
    public TransactionDto addTransaction(Long userId, CreateTransactionDto createTransactionDto) {
        BankTransaction bankTransaction = dtoMapper.transactionDtoToTransaction(createTransactionDto);
        bankTransactionRepository.save(bankTransaction);
        Optional<Category> categoryOptional = categoryRepository.findByName(createTransactionDto.getCategoryName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " does not found"));
        BankCard bankCard = bankCardRepository.findCardByNumber(createTransactionDto.getCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Bank card with number" + createTransactionDto.getCardNumber() + "deos not found "));
        if (categoryOptional.isPresent()) {
            Transaction transaction = Transaction.builder()
                    .bankCard(bankCard)
                    .bankTransaction(bankTransaction)
                    .category(categoryOptional.get())
                    .build();
            Set<BankCard> bankCards = user.getBankCards();
            if (bankCards.contains(bankCard)) {
                transactionRepository.save(transaction);
                return dtoMapper.transactionToDto(transaction);
            } else {
                throw new ResourceNotFoundException("User does not have bank card with number:" + createTransactionDto.getCardNumber());
            }
        } else {
            log.warn("Category with name:{} does not found)", createTransactionDto.getCategoryName());
            throw new ResourceNotFoundException("Category with name:" + createTransactionDto.getCategoryName() + " does not found");
        }
    }

    @Override
    public List<BankCardDto> getBankCardsByUserId(Long clientId) {
        if (userRepository.findById(clientId).isPresent()) {
            List<BankCard> bankCards = bankCardRepository.getBankCardsByUserId(clientId);
            if (!bankCards.isEmpty()) {
                return bankCards.stream()
                        .map(dtoMapper::bankCardToBankCardDto)
                        .toList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public UserHeaderDto getHeaderClientData(Long userId) {
        byte[] avatar = userRepository.findAvatarByUserId(userId);
        UserHeaderDto dto = UserHeaderDto.builder()
                .id(userId)
                .build();
        if (avatar != null && avatar.length > 0) {
            return dto.toBuilder()
                    .avatar(imageConvertor.convertByteImageToString(userRepository.findAvatarByUserId(userId)))
                    .build();
        } else {
            return dto.toBuilder()
                    .avatar(imageService.getDefaultAvatarImage())
                    .build();
        }
    }

    @Override
    @Transient
    @Transactional
    public Image updateAvatar(Long userId, MultipartFile avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        Image image;
        try {
            if (avatar != null) {
                image = new Image(avatar.getBytes());
                imageRepository.save(image);
                user.setAvatar(image);
            } else {
                throw new ImageException("Image does not provided");
            }
        } catch (IOException e) {
            throw new ImageException(e.getMessage());
        }
        userRepository.save(user);
        return image;
    }

    @Override
    public Page<UserDto> findUsersWithFilters(int page, int size, UserSearchCriteria criteria) {
        Sort sort = Sort.by(SORT_PROPERTY_FIRST_NAME);
        Page<User> userPage = userSpecification.getUsersWithCriterias(criteria, page, size, sort);
        return userPage.map(dtoMapper::userToDto);
    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        if (user.getRole() == Role.ROLE_CLIENT) {
            user.setStatus(ActiveStatus.BANNED);
            userRepository.save(user);
        } else {
            throw new SecurityException("User does not have enough permissions to change another user active status");
        }
    }

    @Override
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        if (user.getRole() == Role.ROLE_CLIENT) {
            user.setStatus(ActiveStatus.ACTIVE);
            userRepository.save(user);
        } else {
            throw new SecurityException("User does not have enough permissions to change another user active status");
        }
    }

    @Override
    @Transactional
    public SubscriptionStatus updateUserPlan(Long userId, SubscriptionStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        Subscription subscription = subscriptionRepository.findBySubscriptionStatus(status)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription status with name:" + status.name() + " does not exist"));
        updateSubscription(user, subscription);
        log.info("update user plan. userId:{},user plan{}", userId, status.name());
        userRepository.save(user);
        return subscription.getSubscriptionStatus();
    }

    @Override
    public List<Long> findUserIdsWithExpiredSubscriptions() {
        return userRepository.findUserIdsWithExpiredSubscriptions();
    }

    @Override
    public List<String> findUserEmailsWithExpiredSubscriptions() {
        return userRepository.findUserEmailsWithExpiredSubscriptions();
    }

    @Override
    public Role updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        String startUserRole = user.getRole().toString();
        user.setRole(role);
        userRepository.save(user);
        log.info("update user role from:{} , to:{}", startUserRole, role.toString());
        return user.getRole();
    }

    @Override
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(dtoMapper::userToDto)
                .toList();
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(dtoMapper::userToDto)
                .toList();
    }

    @Override
    public List<UserDto> getUsersByEmailPattern(String email) {
        return userRepository.findByEmailContaining(email).stream()
                .map(dtoMapper::userToDto)
                .toList();
    }

    @Override
    public PageDto<UserDto> getUserPageByEmailPattern(String email, Integer pageNumber, Integer pageSize) {
        List<UserDto> users = userRepository.findByEmailContaining(email).stream()
                .map(dtoMapper::userToDto)
                .toList();
        PageDto<UserDto> userPageDto = PageDto.<UserDto>builder()
                .data(users)
                .page(pageNumber)
                .size(pageSize)
                .totalSize(users.size())
                .build();
        userPageDto = userPageDto.toBuilder()
                .totalPage((int) Math.ceil((double) users.size() / pageSize))
                .build();
        return userPageDto;
    }

    @Override
    public void sendEmailForAllClients(EmailContext email) {
        String[] emails = userRepository.findAllEmailsByRole(Role.ROLE_CLIENT)
                .toArray(String[]::new);
        email.setTo(Arrays.toString(emails));
        emailService.sendEmailWithAttachment(email);
    }

    @Override
    public void setStatus(Long userId, ActiveStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not exist"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public Boolean existByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void setNewPassword(String email, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " does not exist"));
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void authenticateUser(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public Subscription getUserSubscriptionById(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription for user with id " + userId + " does not exist"));
    }

    @Override
    public void assignBankCardToClient(Long userId, BankCard bankCard) throws IllegalArgumentException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        user.getBankCards().stream()
                .anyMatch(bc -> bc.getCardNumber().equals(bankCard.getCardNumber()));
        if (user.getBankCards().size() + 1 > user.getSubscription().getMaxCardsSupport()) {
            log.warn("User with id:{} does not have enough bank cards size({}) limit to add card)", userId, user.getBankCards().size());
            throw new UserPlanException("Client plan does not maintain this functionality");
        }
        user.getBankCards().add(bankCard);
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
        clientStatisticDto.setTotalBalance(getTotalBalance(client, clientStatisticDto));
        return clientStatisticDto;
    }

    private void setTotalIncomeSum(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime tenYearsStartDate = endDate.minusYears(10);
        List<Transaction> lastTenYearsIncome = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), INCOME_TRANSACTION_TYPE, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalIncomeSum(getAllTransactionSum(lastTenYearsIncome));
    }

    private void setTotalExpenseSum(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime tenYearsStartDate = endDate.minusYears(10);
        List<Transaction> lastTenYearsExpense = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), EXPENSE_TRANSACTION_TYPE, tenYearsStartDate, endDate);
        clientStatisticDto.setTotalExpenseSum(getAllTransactionSum(lastTenYearsExpense));
    }

    private BigDecimal getTotalBalance(User client, ClientStatisticDto clientStatisticDto) {
        if (client == null || client.getBankCards() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal incomeSum = clientStatisticDto.getTotalIncomeSum();
        BigDecimal expenseSum = clientStatisticDto.getTotalExpenseSum();
        BigDecimal sum = incomeSum.add(expenseSum);
        return client.getBankCards().stream()
                .map(BankCard::getBalance)
                .filter(Objects::nonNull)
                .reduce(sum, BigDecimal::add);

    }

    private void setLastYearTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneYearStartDate = endDate.minusYears(1);
        List<Transaction> lastYearExpenses = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), EXPENSE_TRANSACTION_TYPE, oneYearStartDate, endDate);
        clientStatisticDto.setExpenses(lastYearExpenses);
        clientStatisticDto.setLastYearExpense(getAllTransactionSum(lastYearExpenses));
        List<Transaction> lastYearIncomes = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), INCOME_TRANSACTION_TYPE, oneYearStartDate, endDate);
        clientStatisticDto.setIncomes(lastYearIncomes);
        clientStatisticDto.setLastYearIncome(getAllTransactionSum(lastYearIncomes));
    }

    private void setLastMonthTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneMonthStartDate = endDate.minusMonths(1);
        List<Transaction> lastMonthExpenses = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), EXPENSE_TRANSACTION_TYPE, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthExpense(getAllTransactionSum(lastMonthExpenses));
        List<Transaction> lastMonthIncomes = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), INCOME_TRANSACTION_TYPE, oneMonthStartDate, endDate);
        clientStatisticDto.setLastMonthIncome(getAllTransactionSum(lastMonthIncomes));
    }

    private void setLastWeekTransactions(Long clientId, LocalDateTime endDate, ClientStatisticDto clientStatisticDto) {
        LocalDateTime oneWeekStartDate = endDate.minusWeeks(1);
        List<Transaction> lastWeekExpenses = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), EXPENSE_TRANSACTION_TYPE, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekExpense(getAllTransactionSum(lastWeekExpenses));
        List<Transaction> lastWeekIncomes = transactionRepository.findTransactionForPeriod(bankCardRepository.getBankCardsByUserId(clientId), INCOME_TRANSACTION_TYPE, oneWeekStartDate, endDate);
        clientStatisticDto.setLastWeekIncome(getAllTransactionSum(lastWeekIncomes));
    }

    private BigDecimal getAllTransactionSum(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getBankTransaction)
                .map(BankTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }

    private void sendConformationEmail(LoginDto loginDto, User user) {
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        emailService.sendConformationEmailRequest(loginDto.getLogin(), confirmation.getToken());
    }

    private User createUserEntity(LoginDto loginDto) {
        if (Optional.ofNullable(loginDto).isEmpty()) {
            throw new ResourceNotFoundException("User is empty");
        }
        if (existByEmail(loginDto.getLogin())) {
            log.warn("user with email already exist email:{}", loginDto.getLogin());
            throw new ResourceAlreadyExistException("Email taken");
        }
        Set<BankCard> bankCards = new HashSet<>();
        Subscription subscription = subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.FREE)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription status does not found in the database"));
        return User.builder()
                .email(loginDto.getLogin())
                .password(encoder.encode(loginDto.getPassword()))
                .bankCards(bankCards)
                .role(Role.ROLE_CLIENT)
                .subscription(subscription)
                .status(ActiveStatus.INACTIVE)
                .build();
    }

    private void updateSubscription(User user, Subscription subscription) {
        if (user.getSubscription().equals(subscription)) {
            Date currentFinishDate = user.getSubscriptionFinishDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentFinishDate);
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            user.setSubscriptionFinishDate(calendar.getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            user.setSubscription(subscription);
            user.setSubscriptionFinishDate(calendar.getTime());
        }
    }
}