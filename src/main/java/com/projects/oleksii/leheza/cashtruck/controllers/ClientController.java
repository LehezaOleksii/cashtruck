package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final EmailService emailService;

    @PostMapping(path = "/login")
    public ModelAndView registerNewClient(@RequestBody CreateUserDto createUserDto) {
        ModelAndView modelAndView;
        if (userService.findByEmail(createUserDto.getEmail()) == null) {
            log.info("start saving user card with email:{}", createUserDto.getEmail());
            User user = userService.saveClient(createUserDto);
            modelAndView = new ModelAndView("redirect:/client/login");
            modelAndView.addObject("client", userService.getHeaderClientData(user.getId()));
            return modelAndView;
        } else {
            log.warn("user with email already exist:{}", createUserDto.getEmail());
            return null;
//            throw new ResourceAlreadyExistException(""); //TODO
        }
    }

    @GetMapping(path = "/{userId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "userId") Long userId) {
        UserHeaderDto client = userService.getHeaderClientData(userId);
        if (client == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        modelAndView.addObject("client", client);
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(userId));
        return modelAndView;
    }

    @GetMapping({"/{userId}/bank_cards/save", "/{userId}/bank_cards/{bankCardId}/save"})
    public ModelAndView clientBankCardsForm(@PathVariable Long userId, @PathVariable(required = false) Long bankCardId) {
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));//TODO use DTO
        modelAndView.addObject("userId", userService.getUserById(userId).getId());//TODO use DTO
        if (Optional.ofNullable(bankCardId).isPresent()) {
            BankCard bankCard = bankCardService.getById(bankCardId);
            modelAndView.addObject("bank_card", CreateBankCardDto.builder().bankName(bankCard.getBankName())
                    .nameOnCard(bankCard.getNameOnCard())
                    .id(bankCardId)
                    .expiringDate(bankCard.getExpiringDate())
                    .cardNumber(bankCard.getCardNumber())
                    .balance(Double.parseDouble(bankCard.getBalance().toString()))
                    .cvv(bankCard.getCvv()).build());
        } else {
            modelAndView.addObject("bank_card", new CreateBankCardDto());
        }
        return modelAndView;
    }

    @PostMapping("/{userId}/bank_cards")
    public ModelAndView saveBankCardToClient(@PathVariable Long userId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the save bank card process. userId:{} ,bank card number{}", userId, bankCardDto.getCardNumber());
            return new ModelAndView("client/add_bank_card")
                    .addObject("client", userService.getHeaderClientData(userId));
        }
        if (!bankCardService.isClientHasCard(userId, bankCardDto)) {
            try {
                BankCard bankCard = bankCardService.save(bankCardDto);
                savingService.assignBankCardToClient(userId, bankCard);
            } catch (IllegalArgumentException e) {
                log.warn("user with id: {} has bank card with number:{}", userId, bankCardDto.getCardNumber());
                return new ModelAndView("redirect:/clients/" + userId + "/premium");
            }
        }
        return new ModelAndView("redirect:/clients/" + userId);
    }

    @GetMapping("/{userId}/bank_cards/update")
    public ModelAndView updateBankCardForm(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/update_delete_bank_card");
        UserHeaderDto userHeaderDto = userService.getHeaderClientData(userId);
        modelAndView.addObject("client", userHeaderDto);
        modelAndView.addObject("bank_cards", userService.getUserById(userId).getSaving().getBankCards());
        return modelAndView;
    }

    @PutMapping("/{userId}/bank_cards")
    public ModelAndView updateBankCardData(@PathVariable Long userId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
//        savingService.assignBankCardToClient(userId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        return new ModelAndView("redirect:/clients/" + userId);
    }

    @GetMapping("/{userId}/bank_cards/{bankCardId}/remove")
    public ModelAndView removeBankCard(@PathVariable Long userId, @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ModelAndView("redirect:/clients/" + userId);
    }

    @GetMapping("/{userId}/categories")
    public ModelAndView viewIncomeAndExpensesDashboard(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/categories");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));//TODO Optimise maybe (it will be a lot of dto)
        modelAndView.addObject("incomes_categories", transactionService.findClientIncomeCategoriesByClientId(userId));
        modelAndView.addObject("expenses_categories", transactionService.findClientExpenseCategoriesByClientId(userId));
        return modelAndView;
    }

    @GetMapping("/{userId}/categories/{categoryName}")
    public ModelAndView viewTransactionsByCategoryName(@PathVariable Long userId,
                                                       @PathVariable String categoryName,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
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

    @GetMapping("/{userId}/profile")
    public ModelAndView updateClientAccountForm(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/profile");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("userId", userId);
        UserUpdateDto userUpdateDto = userService.getClientUpdateDto(userId);
        modelAndView.addObject("clientDto", userUpdateDto);
        return modelAndView;
    }

    @PostMapping("/{userId}/update")
    public ModelAndView updateClientAccount(@PathVariable Long userId, @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
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
            ModelAndView modelAndView = new ModelAndView("redirect:/clients/" + userId + "/profile");
            modelAndView.addObject("client", userService.getHeaderClientData(userId));
            return new ModelAndView("redirect:/clients/" + userId);
        }
    }


    @GetMapping("/{userId}/premium")
    public ModelAndView getPlansList(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/plans");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("client_plan", userService.getUserById(userId).getSubscription());
        modelAndView.addObject("payment_request", new PaymentCreateRequest());
        return modelAndView;
    }

    @GetMapping(path = "/{userId}/emails")
    ModelAndView getEmailsMenu(@PathVariable("userId") Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/emails");
        modelAndView.addObject("client", userService.getUserDto(userId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("managers", userService.getUsersByRole(Role.MANAGER));
        return modelAndView;
    }


    @PostMapping(path = "/{userId}/emails/send")
    ModelAndView sendEmail(@PathVariable("userId") Long userId,
                           @Valid @ModelAttribute("email") EmailContext email) {
        log.info("start sending email to user with email: {}", email.getTo());
        emailService.sendEmailWithAttachment(email);
        log.info("finish sending email to user with email: {}", email.getTo());
        return new ModelAndView("redirect:/clients/" + userId + "/emails");
    }

    @GetMapping(path = "/{userId}/transactions")
    ModelAndView createTransactionForm(@PathVariable("userId") Long userId) {
        ModelAndView modelAndView = new ModelAndView("client/create_transaction");
        modelAndView.addObject("client", userService.getUserDto(userId));
        modelAndView.addObject("incomes", categoryService.findAllIncomeCategories());
        modelAndView.addObject("expenses", categoryService.findAllExpensesCategories());
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        modelAndView.addObject("transaction", new CreateTransactionDto());
        return modelAndView;
    }

    @PostMapping(path = "/{userId}/transactions/save")
    ModelAndView saveTransaction(@PathVariable("userId") Long userId, @ModelAttribute CreateTransactionDto transaction) {
        userService.addTransaction(userId, transaction);
        return new ModelAndView("redirect:/clients/" + userId);
    }
}