package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.domain.Currency;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankIntegration;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.*;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final List<TransactionType> INCOME_TRANSACTION_TYPE_LIST = List.of(
            TransactionType.INCOME,
            TransactionType.UNIVERSAL
    );

    private static final List<TransactionType> EXPENSE_TRANSACTION_TYPE_LIST = List.of(
            TransactionType.EXPENSE,
            TransactionType.UNIVERSAL
    );
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
    private final MonobankIntegrationRepository monobankIntegrationRepository;
    private final CurrencyRepository currencyRepository;

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
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(new User());
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
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
        Optional<Category> categoryOptional = categoryRepository.findByName(createTransactionDto.getCategoryName());
        BankCard bankCard = bankCardRepository.findCardByNumberAndUserId(createTransactionDto.getCardNumber(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank card with number: " + createTransactionDto.getCardNumber() + " deos not found "));
        BankTransaction bankTransaction = dtoMapper.transactionDtoToTransaction(createTransactionDto, bankCard.getCurrency().getDelimiter());
        bankTransactionRepository.save(bankTransaction);
        if (categoryOptional.isPresent()) {
            Transaction transaction = Transaction.builder()
                    .bankCard(bankCard)
                    .bankTransaction(bankTransaction)
                    .category(categoryOptional.get())
                    .build();
            List<BankCard> bankCards = bankCardRepository.getBankCardsByUserId(userId);
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
    public List<DashboardBankCardDto> getDashboardBankCardsDtoByUserId(Long userId) {
        return userRepository.findById(userId).map(user -> {
            List<BankCard> bankCards = bankCardRepository.getBankCardsByUserId(userId);
            List<DashboardBankCardDto> bankCardsDtos = bankCards.stream()
                    .map(dtoMapper::bankCardToDashboardBankCardDto)
                    .toList();
            monobankIntegrationRepository.findByUserId(userId)
                    .map(MonobankIntegration::getMonobankAccounts)
                    .ifPresent(monoAccounts -> {
                        bankCardsDtos.stream()
                                .filter(dto -> monoAccounts.stream()
                                        .map(MonobankAccount::getMaskedPan)
                                        .anyMatch(maskedPan -> maskedPan.equals(dto.getCardNumber())))
                                .forEach(dto -> {
                                    dto.setBank("Monobank");
                                    monoAccounts.stream()
                                            .filter(acc -> acc.getMaskedPan().equals(dto.getCardNumber()))
                                            .findFirst()
                                            .ifPresent(acc -> dto.setType(acc.getType())); // встановлюємо type
                                });
                    });

            return bankCardsDtos;
        }).orElseGet(ArrayList::new);
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
        if (!user.getBankCards().contains(bankCard)) {
            user.getBankCards().add(bankCard);
            userRepository.save(user);
        }
        if (bankCard.getUser() == null || !bankCard.getUser().equals(user)) {
            bankCard.setUser(user);
            bankCardRepository.save(bankCard);
        }
    }

    private ClientStatisticDto createStatisticDto(User client) {
        boolean isPositiveTransactionSumIncome = true;
        boolean isPositiveTransactionSumExpense = false;
        String currencyName = "UAH";
        Currency currency = currencyRepository.findByShortName(currencyName).orElseThrow(() -> new ResourceNotFoundException("Currency with name: " + currencyName + " not found"));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYear = now.minusYears(1);
        LocalDateTime oneMonth = now.minusMonths(1);
        LocalDateTime twoMonth = now.minusMonths(2);
        List<TransactionDto> lastYearIncome = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), oneYear, now, isPositiveTransactionSumIncome)
                .stream()
                .map(dtoMapper::transactionToDto)
                .toList();
        List<TransactionDto> lastYearExpense = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), oneYear, now, isPositiveTransactionSumExpense)
                .stream()
                .map(dtoMapper::transactionToDto)
                .toList();
        List<BankCard> bankCards = bankCardRepository.getBankCardsByUserId(client.getId());
        int delimiter = currency.getDelimiter();
        long totalBalance = bankCards.stream()
                .flatMap(card -> card.getTransactions().stream())
                .filter(transaction -> transaction.getBankTransaction().getCurrency().equals(currency))
                .mapToLong(transaction -> transaction.getBankTransaction().getSum())
                .sum();
        long lastMonthIncomes = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), oneMonth, now, isPositiveTransactionSumIncome).stream()
                .mapToLong(t -> t.getBankTransaction().getSum())
                .sum();
        long previous2MonthIncomes = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), twoMonth, oneMonth, isPositiveTransactionSumIncome).stream()
                .mapToLong(t -> t.getBankTransaction().getSum())
                .sum();
        long lastMonthExpenses = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), oneMonth, now, isPositiveTransactionSumExpense).stream()
                .mapToLong(t -> t.getBankTransaction().getSum())
                .sum();
        long previous2MonthExpenses = transactionRepository.findByBankCardsAndDateRangeAndTransactionTypes(bankCardRepository.getBankCardsByUserId(client.getId()), twoMonth, oneMonth, isPositiveTransactionSumExpense).stream()
                .mapToLong(t -> t.getBankTransaction().getSum())
                .sum();
        long lastMonthProfit = lastMonthIncomes + lastMonthExpenses;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);
        int lastMonthAmount = 6;
        Map<String, Double> totalBalanceGraphic = getTotalBalanceGraphic(client.getId(), lastMonthAmount, formatter, delimiter);
        List<DashboardCategoryDto> categoriesDiagram = getUserCategorySummaryLast6Months(client.getId());
        int lastTransactionsPageNumber = 0;
        int lastTransactionsPageSize = 5;
        List<DashboardTransactionDto> lastTransactions = getLastUserTransactions(client.getId(), lastTransactionsPageNumber, lastTransactionsPageSize);
        DashboardIncomeExpensesDiagramDto incomeExpensesDiagramDto = DashboardIncomeExpensesDiagramDto.builder()
                .incomes(getMonthSumMap(client.getId(), isPositiveTransactionSumIncome, formatter, lastMonthAmount, delimiter))
                .expenses(getMonthSumMap(client.getId(), isPositiveTransactionSumExpense, formatter, lastMonthAmount, delimiter))
                .build();
        double totalBalanceDenominator = totalBalance - (lastMonthIncomes + lastMonthExpenses);
        double totalBalancePercentage = totalBalanceDenominator != 0
                ? ((totalBalance * 100.0) / totalBalanceDenominator) - 100
                : 0.0;
        double lastMonthIncomesPercentage = previous2MonthIncomes != 0
                ? ((lastMonthIncomes * 100.0) / previous2MonthIncomes) - 100
                : 0.0;
        double lastMonthExpensesPercentage = previous2MonthExpenses != 0
                ? ((lastMonthExpenses * 100.0) / previous2MonthExpenses) - 100
                : 0.0;

        double previousMonthProfit = previous2MonthIncomes + previous2MonthExpenses;
        double currentMonthProfit = lastMonthIncomes + lastMonthExpenses;

        double percentChange = ((currentMonthProfit - previousMonthProfit)
                / Math.abs(previousMonthProfit))
                * 100.0;

        System.out.printf("Зростання: %.2f%%\n", percentChange);

        double lastMonthProfitPercentage;
        if (previousMonthProfit == 0) {
            lastMonthProfitPercentage = 0;
        } else {
            lastMonthProfitPercentage =
                    ((currentMonthProfit - previousMonthProfit) / Math.abs(previousMonthProfit)) * 100.0;
        }

        ClientStatisticDto clientStatisticDto = new ClientStatisticDto().toBuilder()
                .expenses(lastYearExpense)
                .incomes(lastYearIncome)
                .totalBalance((double) totalBalance / delimiter)
                .totalBalancePercentage(totalBalancePercentage)
                .lastMonthIncomes((double) lastMonthIncomes / delimiter)
                .lastMonthIncomesPercentage(lastMonthIncomesPercentage)
                .lastMonthExpenses((double) lastMonthExpenses / delimiter)
                .lastMonthExpensesPercentage(lastMonthExpensesPercentage)
                .lastMonthProfit((double) lastMonthProfit / delimiter)
                .lastMonthProfitPercentage(lastMonthProfitPercentage)
                .totalBalanceGraphic(totalBalanceGraphic)
                .categoriesDiagram(categoriesDiagram)
                .lastTransactions(lastTransactions)
                .incomeExpensesDiagram(incomeExpensesDiagramDto)
                .delimiter(currency.getDelimiter())
                .build();
        return clientStatisticDto;
    }

    private List<DashboardTransactionDto> getLastUserTransactions(Long userId, int pageNumber, int pageSize) {
        Pageable topFive = PageRequest.of(pageNumber, pageSize);
        return transactionRepository.findLastTransactionsByUserId(userId, topFive);
    }

    private List<DashboardCategoryDto> getUserCategorySummaryLast6Months(Long userId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        return categoryRepository.getCategorySums(userId, sixMonthsAgo);
    }

    private Map<String, Double> getTotalBalanceGraphic(
            Long userId,
            int lastMonthAmount,
            DateTimeFormatter formatter,
            int delimiter) {

        // 1. Вираховуємо початковий баланс до старту (now() - lastMonthAmount місяців)
        LocalDateTime periodStart = LocalDateTime.now().minusMonths(lastMonthAmount);
        double initialIncome = sumTransactionsBefore(userId, true, periodStart) / delimiter;
        double initialExpense = sumTransactionsBefore(userId, false, periodStart) / delimiter;
        double runningBalance = initialIncome - initialExpense;

        // 2. Збираємо нетто-суми за кожен із останніх lastMonthAmount місяців
        Map<String, Double> incomeByMonth = getMonthSumMap(userId, true, formatter, lastMonthAmount, delimiter);
        Map<String, Double> expenseByMonth = getMonthSumMap(userId, false, formatter, lastMonthAmount, delimiter);

        // нетто = дохід − витрати
        Map<String, Double> netByMonth = new HashMap<>();
        for (String month : incomeByMonth.keySet()) {
            double income = incomeByMonth.getOrDefault(month, 0.0);
            double expense = expenseByMonth.getOrDefault(month, 0.0);
            netByMonth.put(month, income - expense);
        }

        // 3. Сортуємо місяці в хронологічному порядку
        List<String> monthOrder = Arrays.asList(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        );
        Map<String, Double> sortedNet = netByMonth.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> monthOrder.indexOf(e.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // 4. Обчислюємо кумулятивний баланс
        Map<String, Double> cumulativeBalance = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : sortedNet.entrySet()) {
            runningBalance += e.getValue();
            cumulativeBalance.put(e.getKey(), runningBalance);
        }

        return cumulativeBalance;
    }

    /**
     * Допоміжний метод: підсумовує транзакції до певної дати
     */
    private double sumTransactionsBefore(
            Long userId,
            boolean isIncome,
            LocalDateTime before) {

        // Замінюємо LocalDateTime.MIN на нормальну дату
        LocalDateTime startDate = LocalDateTime.of(1970, 1, 1, 0, 0);

        return transactionRepository
                .findByBankCardsAndDateRangeAndTransactionTypes(
                        bankCardRepository.getBankCardsByUserId(userId),
                        startDate,
                        before,
                        isIncome)
                .stream()
                .mapToDouble(t -> (double) t.getBankTransaction().getSum())
                .sum();
    }


    private Map<String, Double> getMonthSumMap(Long userId, boolean isPositiveTransactionSum, DateTimeFormatter formatter, int lastMonthAmount, int delimiter) {
        return transactionRepository
                .findByBankCardsAndDateRangeAndTransactionTypes(
                        bankCardRepository.getBankCardsByUserId(userId),
                        LocalDateTime.now().minusMonths(lastMonthAmount),
                        LocalDateTime.now(),
                        isPositiveTransactionSum)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> t.getBankTransaction().getTime().format(formatter),
                        TreeMap::new,
                        Collectors.summingDouble(t -> (double) t.getBankTransaction().getSum() / delimiter)
                ));
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
        MonobankIntegration monobankIntegration = new MonobankIntegration();
        monobankIntegrationRepository.save(monobankIntegration);
        return User.builder()
                .email(loginDto.getLogin())
                .password(encoder.encode(loginDto.getPassword()))
                .bankCards(bankCards)
                .role(Role.ROLE_CLIENT)
                .subscription(subscription)
                .monobankIntegration(monobankIntegration)
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