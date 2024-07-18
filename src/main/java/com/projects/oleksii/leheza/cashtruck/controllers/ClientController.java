package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
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

import java.util.Optional;

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
    private final UserRepository userRepository;

    @GetMapping(path = "/dashboard")
    public ModelAndView showClientDashboard(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(userId));
        return modelAndView;
    }

    @GetMapping({"/bank_cards"})
    public ModelAndView clientBankCardsForm(@RequestParam(required = false) Long bankCardId,
                                            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (bankCardId != null && !bankCardService.isClientHasCard(userId, bankCardId)) {
            throw new SecurityException("user has not card with id:" + bankCardId);
        }
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        if (Optional.ofNullable(bankCardId).isPresent()) {
            modelAndView.addObject("bank_card", bankCardService.getById(bankCardId));
        } else {
            modelAndView.addObject("bank_card", new BankCardDto());
        }
        return modelAndView;
    }

    @PostMapping("/bank_cards/save")
    public ModelAndView saveBankCardToClient(@Valid @ModelAttribute("bank_card") BankCardDto bankCardDto,
                                             @AuthenticationPrincipal User user,
                                             BindingResult bindingResult) {
        //TODO bindingResult and valid checking
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
    public ModelAndView removeBankCard(@RequestParam Long bankCardId,
                                       @AuthenticationPrincipal User user) {
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
    public ModelAndView viewTransactionsByCategoryName(@PathVariable String categoryName,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                       @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/transactions_details");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("category", categoryService.findByName(categoryName));
        Page<TransactionDto> transactionPage = transactionService.findTransactionsByClientIdAndCategoryName(userId, categoryName, page, size);
        modelAndView.addObject("currentPage", transactionPage.getNumber());
        modelAndView.addObject("totalPages", transactionPage.getTotalPages());
        modelAndView.addObject("transactions", transactionPage);
        //TODO exceptions with page number
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
    public ModelAndView updateClientAccount(@Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto,
                                            BindingResult bindingResult,
                                            @RequestParam("image") MultipartFile avatar,
                                            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the update client account. userId:{}", userId);
            return new ModelAndView("client/profile")
                    .addObject("client", userService.getHeaderClientData(userId))
                    .addObject("userId", userId);
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
    ModelAndView sendEmail(@Valid @ModelAttribute("email") EmailContext email,
                           @AuthenticationPrincipal User user) { //TODO
        log.info("start sending email to user with email: {}", email.getTo());
        emailService.sendEmailWithAttachment(email);
        return new ModelAndView("redirect:/clients/emails");
    }

    @GetMapping(path = "/transactions")
    ModelAndView createTransactionForm(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("client/create_transaction");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("incomes", categoryService.findAllIncomeCategories());
        modelAndView.addObject("expenses", categoryService.findAllExpensesCategories());
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
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
    ModelAndView updateSubscriptionPlan(@PathVariable("subscriptionStatus") String subscriptionStatus,
                                        @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(subscriptionStatus));
        log.info("User status was updated. user id:{}, user status:{}", userId, subscriptionStatus);
        return new ModelAndView("redirect:/clients/premium");
    }
}