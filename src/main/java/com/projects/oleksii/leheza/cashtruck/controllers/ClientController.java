package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankClientInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankRequestPersonalClientDataDto;
import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.DashboardBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.bank.monobank.MonobankRequestService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/clients")
public class ClientController {

    private final UserService userService;
    private final BankCardService bankCardService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final EmailService emailService;
    private final CurrencyService currencyService;
    private final MonobankIntegrationService monobankIntegrationService;
    private final MonobankAccountService monobankAccountService;
    private final MonobankRequestService monobankRequestService;
    private final MonobankTransactionService monobankTransactionService;

    @GetMapping(path = "/dashboard")
    public ModelAndView showClientDashboard(@AuthenticationPrincipal User user, @RequestParam(value = "cardNumber", required = false) String selectedCardNumber) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/dashboard");

        List<DashboardBankCardDto> cards = userService.getDashboardBankCardsDtoByUserId(userId);
        modelAndView.addObject("bank_cards", cards);
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(userId));

        if (selectedCardNumber != null) {
            DashboardBankCardDto selectedCard = cards.stream().filter(c -> c.getCardNumber().equals(selectedCardNumber)).findFirst().orElse(null);
            modelAndView.addObject("bank_card", selectedCard);
        } else if (!cards.isEmpty()) {
            modelAndView.addObject("bank_card", cards.get(0));
        }
        return modelAndView;
    }


    @GetMapping({"/bank_cards"})
    public ModelAndView clientBankCardsForm(@RequestParam(required = false) Long bankCardId, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (bankCardId != null && !bankCardService.isClientHasCard(userId, bankCardId)) {
            throw new SecurityException("user has not card with id:" + bankCardId);
        }
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card_manually");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("currency_short_names", currencyService.getCurrenciesShortNames());
        if (Optional.ofNullable(bankCardId).isPresent()) {
            modelAndView.addObject("bank_card", bankCardService.getById(bankCardId));
        } else {
            modelAndView.addObject("bank_card", new BankCardDto());
        }
        return modelAndView;
    }

    @GetMapping({"/bank_cards/add/menu"})
    public ModelAndView clientAddBankCard(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        return modelAndView;
    }

    @GetMapping({"/bank_cards/add/monobank"})
    public ModelAndView addMonobankCardPage(@ModelAttribute("error") String error, RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/add_monobank_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        if (error != null && !error.isEmpty()) {
            return modelAndView;
        }
        MonobankRequestPersonalClientDataDto requestDto;
        try {
            requestDto = monobankRequestService.requestToPersonalClientData(userId);
        } catch (Exception e) {
            log.error(e.getMessage() + "; casue: " + e.getCause());
            redirectAttributes.addFlashAttribute("error", "Can not get connection with Monobank service.");
            return new ModelAndView("redirect:/clients/bank_cards/add/monobank");
        }
        if (Optional.ofNullable(requestDto).isPresent() && requestDto.getAcceptUrl() != null && !requestDto.getTokenRequestId().isEmpty()) {
            modelAndView.addObject("accept_url", requestDto.getAcceptUrl());
            monobankIntegrationService.setMonobankRequestId(userId, requestDto.getTokenRequestId());
        } else {
            log.error("Can not retrieve accept url from monobank request");
            redirectAttributes.addFlashAttribute("error", "Can not get Monobank qr code.");
            return new ModelAndView("redirect:/clients/bank_cards/add/monobank");
        }
        return modelAndView;
    }

    @PostMapping("/bank_cards/add/monobank/token/connect")
    public ModelAndView saveMonobankRequestId(RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        user = userService.getUserById(userId);
        if (!Optional.ofNullable(user).isPresent()) {
            throw new SecurityException("user with id:" + userId + " does not exists");
        }
        String requestId = user.getMonobankIntegration().getRequestId();
        MonobankClientInfoDto monobankClientInfoDto = null;
        if (requestId != null && monobankRequestService.checkAccessToUserData(requestId)) {
            try {
                monobankClientInfoDto = monobankRequestService.getMonobankClientInfo(requestId);
                if (monobankClientInfoDto == null) {
                    redirectAttributes.addFlashAttribute("error", "Monobank data access is denied.");
                }
            } catch (Exception e) {
                log.error(e.getMessage() + "; casue: " + e.getCause());
                redirectAttributes.addFlashAttribute("warn", "Please confirm permission request in Monobank application.");
            }
        } else {
            redirectAttributes.addFlashAttribute("warn", "Please confirm permission request in Monobank application.");
        }
        if (monobankClientInfoDto != null && monobankClientInfoDto.getAccounts() != null && !monobankClientInfoDto.getAccounts().isEmpty()) {
            monobankAccountService.saveMonobankAccounts(userId, monobankClientInfoDto.getAccounts());
        } else {
            redirectAttributes.addFlashAttribute("warn", "Please confirm permission request in Monobank application.");
        }
        if (redirectAttributes.containsAttribute("error") || redirectAttributes.containsAttribute("warn")) {
            return new ModelAndView("redirect:/clients/bank_cards/add/monobank");
        }
        return new ModelAndView("redirect:/clients/bank_cards/add/monobank/cards");
    }

    @GetMapping({"/bank_cards/add/monobank/cards"})
    public ModelAndView getMonobankCards(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/monobank_cards");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("monobank_cards", monobankAccountService.findByUserId(userId));
        return modelAndView;
    }

    @GetMapping({"/bank_cards/add/monobank/cards/save"})
    public ModelAndView synchronizeMonobankCards(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes, @RequestParam("selectedPans") List<String> selectedPans) {
        Long userId = user.getId();
        user = userService.getUserById(userId);
        String requestId = user.getMonobankIntegration().getRequestId();
        Set<MonobankAccount> monobankAccounts = new HashSet<>(monobankAccountService.findByPans(selectedPans));
        monobankIntegrationService.saveMonobankAccountsAsBankCards(userId, monobankAccounts);
        long timeNow = Instant.now().toEpochMilli();
        try {
            int maxTransactionsResponseAmount = 500;
            for (MonobankAccount monobankAccount : monobankAccounts) {
                boolean isAccountHasTransactions = !monobankAccountService.getAllTransactionsIds(monobankAccount.getMonobankId()).isEmpty();
                String monobankAccountId = monobankAccount.getMonobankId();
                List<MonobankAccountTransactionDto> transactions = monobankRequestService.getClientStatementInfo(requestId, monobankAccountId, timeNow);
                saveUniqueTransactions(monobankAccount, transactions, userId);
                while (transactions.size() == maxTransactionsResponseAmount) {
                    MonobankAccountTransactionDto lastTransaction = transactions.get(maxTransactionsResponseAmount - 1);
                    Long lastTransactionTime = lastTransaction.getTime();
                    transactions = monobankRequestService.getClientStatementInfo(requestId, monobankAccountId, lastTransactionTime);
                    saveUniqueTransactions(monobankAccount, transactions, userId);
                }
                if (isAccountHasTransactions) {
                    monobankAccountService.setMonobankAccountBalance(monobankAccountId, userId, monobankAccount.getBalance());
                }
            }
        } catch (HttpTimeoutException e) {
            redirectAttributes.addFlashAttribute("error", "Request to Monobank API timed out.");
            log.error("Request to Monobank API timed out: {}", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "IO Exception while calling Monobank API.");
            log.error("IO Exception while calling Monobank API: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            redirectAttributes.addFlashAttribute("error", "Request to Monobank API was interrupted.");
            log.error("Request to Monobank API was interrupted: {}", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error while fetching Monobank client info.");
            log.error("Unexpected error while fetching Monobank client info: {}", e.getMessage());
        } if (redirectAttributes.containsAttribute("error")) {
            return new ModelAndView("redirect:/clients/bank_cards/add/monobank/cards");
        }
        return new ModelAndView("redirect:/clients/dashboard");
    }

    private void saveUniqueTransactions(MonobankAccount monobankAccount, List<MonobankAccountTransactionDto> transactions, Long userId) {
        List<String> monobankTransactionIds = monobankAccountService.getAllTransactionsIds(monobankAccount.getMonobankId());
        transactions.forEach(transaction -> {
            if (!monobankTransactionIds.contains(transaction.getId())) {
                Transaction savedTransaction = transactionService.save(transaction, monobankAccount.getMaskedPan(), userId);
                monobankTransactionService.save(transaction, savedTransaction);
            }
        });
    }

    @GetMapping({"/bank_cards/add/usa_canada_banks"})
    public ModelAndView addUsaCanadaCardPage(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/add_usa_canada_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        return modelAndView;
    }

    @PostMapping("/bank_cards/save")
    public ModelAndView saveBankCardToClient(@Valid @ModelAttribute("bank_card") BankCardDto bankCardDto, @AuthenticationPrincipal User user, BindingResult bindingResult) {
        Long userId = user.getId();
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the save bank card process. userId:{} ,bank card number{}", userId, bankCardDto.getCardNumber());
            return new ModelAndView("redirect:/clients/bank_cards");
        }
        BankCard bankCard = bankCardService.save(bankCardDto);
        if (!bankCardService.isClientHasCard(userId, bankCardDto.getCardNumber())) {
            userService.assignBankCardToClient(userId, bankCard);
        }
        return new ModelAndView("redirect:/clients/dashboard");
    }

    @GetMapping("/bank_cards/update")
    public ModelAndView updateBankCardForm(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/update_delete_bank_card");
        UserHeaderDto userHeaderDto = userService.getHeaderClientData(userId);
        modelAndView.addObject("client", userHeaderDto);
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        return modelAndView;
    }

    @GetMapping("/bank_cards/remove")
    public ModelAndView removeBankCard(@RequestParam Long bankCardId, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (!bankCardService.isClientHasCard(userId, bankCardId)) {
            throw new SecurityException("user has not card with id:" + bankCardId);
        }
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ModelAndView("redirect:/clients/dashboard");
    }

    @GetMapping("/categories")
    public ModelAndView viewIncomeAndExpensesDashboard(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/categories");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("incomes_categories", transactionService.findClientIncomeCategoriesByClientId(userId));
        modelAndView.addObject("expenses_categories", transactionService.findClientExpenseCategoriesByClientId(userId));
        return modelAndView;
    }

    @GetMapping("/categories/{categoryName}")
    public ModelAndView viewTransactionsByCategoryName(@PathVariable String categoryName, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/transactions_details");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("category", categoryService.findByName(categoryName));
        Page<TransactionDto> transactionPage = transactionService.findTransactionsByClientIdAndCategoryName(userId, categoryName, page, size);
        modelAndView.addObject("currentPage", transactionPage.getNumber());
        modelAndView.addObject("totalPages", transactionPage.getTotalPages());
        modelAndView.addObject("transactions", transactionPage);
        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView updateClientAccountForm(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/profile");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("clientDto", userService.getClientUpdateDto(userId));
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView updateClientAccount(@Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the update client account. userId:{}", userId);
            return new ModelAndView("client/profile").addObject("client", userService.getHeaderClientData(userId)).addObject("userId", userId);
        } else {
            if (!avatar.isEmpty()) {
                userService.updateAvatar(userId, avatar);
            }
            userService.updateUserInfo(userId, userUpdateDto);
            log.info("update client information. client id:{}", userId);
            ModelAndView modelAndView = new ModelAndView("redirect:/clients/profile");
            modelAndView.addObject("client", userService.getHeaderClientData(userId));
            return new ModelAndView("redirect:/clients/dashboard");
        }
    }


    @GetMapping("/premium")
    public ModelAndView getPlansList(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/plans");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("client_plan", userService.getUserSubscriptionById(userId).getSubscriptionStatus().name());
        modelAndView.addObject("payment_request", new PaymentCreateRequest());
        return modelAndView;
    }

    @GetMapping(path = "/emails")
    ModelAndView getEmailsMenu(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/emails");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("managers", userService.getUsersByRole(Role.ROLE_MANAGER));
        return modelAndView;
    }

    @PostMapping(path = "/emails/send")
    ModelAndView sendEmail(@Valid @ModelAttribute("email") EmailContext email) {
        log.info("start sending email to user with email: {}", email.getTo());
        emailService.sendEmailWithAttachment(email);
        return new ModelAndView("redirect:/clients/emails");
    }

    @GetMapping(path = "/transactions")
    ModelAndView createTransactionForm(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/create_transaction");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("incomes", categoryService.getIncomeAndUniversalCategories());
        modelAndView.addObject("expenses", categoryService.getExpenseAndUniversalCategories());
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        modelAndView.addObject("currencies", currencyService.getCurrenciesShortNames());
        modelAndView.addObject("transaction", new CreateTransactionDto());
        return modelAndView;
    }

    @PostMapping(path = "/transactions/save")
    ModelAndView saveTransaction(@ModelAttribute CreateTransactionDto transaction, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userService.addTransaction(userId, transaction);
        return new ModelAndView("redirect:/clients/dashboard");
    }

    @GetMapping(path = "/premium/plan/{subscriptionStatus}")
    ModelAndView updateSubscriptionPlan(@PathVariable("subscriptionStatus") String subscriptionStatus, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(subscriptionStatus));
        log.info("User status was updated. user id:{}, user status:{}", userId, subscriptionStatus);
        return new ModelAndView("redirect:/clients/premium");
    }
}