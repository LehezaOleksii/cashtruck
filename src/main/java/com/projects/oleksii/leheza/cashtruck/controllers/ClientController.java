package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {

    private final UserService userService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final EmailService emailService;
    private String stripePublicKey = "pk_test_51PDMQi2N1Bginh2bUyYqHhWzip5F9uR8B2BFcGtBorVSDcThERpj5eNYK4gOqrGyUaoQ79aZdb8e0Lt7RfnLsFaM00Qhes8t5J";


    @PostMapping(path = "/login")
    public ModelAndView registerNewClient(@RequestBody CreateUserDto createUserDto) {
        ModelAndView modelAndView;
        if (userService.findByEmail(createUserDto.getEmail()) != null) {
            User user = userService.saveClient(createUserDto);
            modelAndView = new ModelAndView("redirect:/client/login");
            modelAndView.addObject("client", userService.getHeaderClientData(user.getId()));
            //TODO error email is already taken
            return modelAndView;
        } else {
            return null; //TODO
        }
    }

    @GetMapping(path = "/{userId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "userId") Long clientId) {
        UserHeaderDto client = userService.getHeaderClientData(clientId);
        if (client == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(clientId));
        modelAndView.addObject("client", client);
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(clientId));
        return modelAndView;
    }

    @GetMapping({"/{userId}/bank_cards/save", "/{userId}/bank_cards/{bankCardId}/save"})
    public ModelAndView clientBankCardsForm(@PathVariable Long userId, @PathVariable(required = false) Long bankCardId) {
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", userService.getHeaderClientData(userId));//TODO use DTO
        modelAndView.addObject("clientId", userService.getUserById(userId).getId());//TODO use DTO
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

    @PostMapping("/{clientId}/bank_cards")
    public ModelAndView saveBankCardToClient(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("client/add_bank_card")
                    .addObject("client", userService.getHeaderClientData(clientId));
        }
        if (!bankCardService.isClientHasCard(clientId, bankCardDto)) {
            try {
                BankCard bankCard = bankCardService.save(bankCardDto);
                savingService.assignBankCardToClient(clientId, bankCard);
            } catch (IllegalArgumentException e) {
                return new ModelAndView("redirect:/clients/" + clientId + "/premium");
            }
        }
        return new ModelAndView("redirect:/clients/" + clientId);
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
        modelAndView.addObject("clientId", userId);
        UserUpdateDto userUpdateDto = userService.getClientUpdateDto(userId);
        modelAndView.addObject("clientDto", userUpdateDto);
        return modelAndView;
    }

    @PostMapping("/{userId}/update")
    public ModelAndView updateClientAccount(@PathVariable Long userId, @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("client/profile")
                    .addObject("client", userService.getHeaderClientData(userId))
                    .addObject("clientId", userId);
        } else {
            if (!avatar.isEmpty()) {
                userService.updateAvatar(userId, avatar);
            }
            userService.updateUserInfo(userId, userUpdateDto);
            ModelAndView modelAndView = new ModelAndView("redirect:/clients/" + userId + "/profile");
            modelAndView.addObject("client", userService.getHeaderClientData(userId));
            return new ModelAndView("redirect:/clients/" + userId);
        }
    }

    @GetMapping("/{clientId}/premium")
    public ModelAndView getPlansList(@PathVariable Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/plans");
        modelAndView.addObject("client", userService.getHeaderClientData(clientId));
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("client_plan", userService.getUserById(clientId).getSubscription());
        modelAndView.addObject("payment_request", new PaymentCreateRequest());
        modelAndView.addObject("stripePublicKey", stripePublicKey);
        return modelAndView;
    }

    @GetMapping(path = "/{clientId}/emails")
    ModelAndView getEmailsMenu(@PathVariable("clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/emails");
        modelAndView.addObject("client", userService.getUserDto(clientId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("managers",userService.getUsersByRole(Role.MANAGER));
        return modelAndView;
    }


    @PostMapping(path = "/{clientId}/emails/send")
    ModelAndView sendEmail(@PathVariable("clientId") Long clientId,
                           @Valid @ModelAttribute("email") EmailContext email) {
        emailService.sendEmailWithAttachment(userService.getUserById(clientId).getEmail(), email);
        ModelAndView modelAndView = new ModelAndView("client/emails");
        modelAndView.addObject("client", userService.getUserDto(clientId));
        modelAndView.addObject("email", new EmailContext());
        return new ModelAndView("redirect:/clients/" + clientId + "/emails");
    }
}