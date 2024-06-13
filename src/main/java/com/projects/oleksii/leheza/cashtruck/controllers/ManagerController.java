package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.SecurityException;
import com.projects.oleksii.leheza.cashtruck.service.email.EmailServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/managers")
public class ManagerController {

    // TODO     @Resource
    private final UserService userService;
    private final ImageConvertor imageConvertor;
    private final EmailServiceImpl emailService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @PutMapping(path = "/update/{userId}")
    ModelAndView updateManagerInfo(@PathVariable("userId") Long managerId, @ModelAttribute("manager") UserUpdateDto userUpdateDto) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Start Updating manager info. User email: {}", userUpdateDto.getEmail());
        userService.updateUserInfo(managerId, userUpdateDto);
        return modelAndView;
    }

//    @GetMapping(path = "/{managerId}")
//    ModelAndView showManagerDashboard(@PathVariable("managerId") Long managerId) {
//        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
//        modelAndView.addObject("managerId", managerId);
////        modelAndView.addObject("manager", userService.(managerId));
//        return modelAndView;
//    }

    @GetMapping(path = "/{managerId}/users")
    ModelAndView getClientsList(@PathVariable("managerId") Long managerId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size) {
        ModelAndView modelAndView;
        if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.MANAGER)) {
            modelAndView = new ModelAndView("manager/users");
        } else if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.ADMIN)) {
            modelAndView = new ModelAndView("admin/users");
        } else {
            throw new SecurityException("User does not have enough permission for this action");
        }
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        Page<UserDto> usersPage = userService.findAll(page, size);
        modelAndView.addObject("users", usersPage.getContent());
        modelAndView.addObject("currentPage", usersPage.getNumber());
        modelAndView.addObject("totalPages", usersPage.getTotalPages());
        modelAndView.addObject("filterCriteria", new UserSearchCriteria());
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{userId}")
    ModelAndView getClientById(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        ModelAndView modelAndView;
        if (userService.getUserById(userId).getRole().equals((Role.CLIENT.toString()))) {
            modelAndView = new ModelAndView("manager/client_info");
        } else {
            modelAndView = new ModelAndView("manager/manager_info");
        }
        modelAndView.addObject("user", userService.getUserById(userId));
        modelAndView.addObject("manager", userService.getUserById(managerId));
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{userId}/update/form")
    ModelAndView updateClientFormById(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        ModelAndView modelAndView = new ModelAndView("manager/client_info_edit");
        modelAndView.addObject("user", userService.getUserById(userId));
        modelAndView.addObject("manager", userService.getUserById(managerId));
        UserUpdateDto userUpdateDto = userService.getClientUpdateDto(userId);
        modelAndView.addObject("clientDto", userUpdateDto);
        modelAndView.addObject("statuses", ActiveStatus.values());
        return modelAndView;
    }


    @PostMapping("/{managerId}/users/{userId}/update")
    public ModelAndView updateClientAccount(@PathVariable Long managerId,
                                            @PathVariable Long userId,
                                            @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the update client account. userId:{}", userId);
            return new ModelAndView("manager/client_info_edit")
                    .addObject("manager", userService.getHeaderClientData(managerId))
                    .addObject("user", userService.getUserById(userId))
                    .addObject("managerId", managerId)
                    .addObject("statuses", ActiveStatus.values());

        } else {
            log.info("Start Updating client info. User email: {}", userUpdateDto.getEmail());
            userService.updateUserInfo(userId, userUpdateDto);
            return new ModelAndView("redirect:/managers/" + managerId + "/users/" + userId);
        }
    }

    @GetMapping(path = "/{managerId}/users/{userId}/block")
    RedirectView blockUser(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        log.info("Start blocking user. User id: {}", userId);
        userService.blockUser(userId);
        return new RedirectView("/managers/" + managerId + "/users/" + userId);
    }

    @GetMapping(path = "/{managerId}/users/{userId}/unblock")
    RedirectView unblockUser(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        log.info("Start unblocking user. User id: {}", userId);
        userService.unblockUser(userId);
        return new RedirectView("/managers/" + managerId + "/users");
    }

    @GetMapping(path = "/{managerId}/users/{userId}/profile")
    ModelAndView getClientProfileForm(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        ModelAndView modelAndView;
        if (userService.getUserById(userId).getRole().equals((Role.CLIENT.toString()))) {
            modelAndView = new ModelAndView("manager/client_profile");
            modelAndView.addObject("client", userService.getClientUpdateDto(userId));
        } else {
            modelAndView = new ModelAndView("manager/manager_info");
            modelAndView.addObject("user", userService.getUserById(userId));
        }
        modelAndView.addObject("user", userService.getUserById(userId));
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/users/{clientId}/profile")
    ModelAndView changeClientProfile(@PathVariable("managerId") Long managerId, @PathVariable("clientId") Long clientId, @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the update client account by manager. userId:{}", clientId);
            return new ModelAndView("manager/client_profile")
                    .addObject("manager", userService.getHeaderClientData(clientId))
                    .addObject("clientId", clientId)
                    .addObject("managerId", managerId);

        } else {
            log.info("Start changing user account. User id: {}", clientId);
            if (!avatar.isEmpty()) {
                try {
                    userUpdateDto.setAvatar(imageConvertor.convertByteImageToString(avatar.getBytes()));
                } catch (IOException e) {
                    return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId)
                            .addObject("errorMessage", e.getMessage());
                }
            }
            try {
                userService.updateUserInfo(clientId, userUpdateDto);
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId + "/profile");
                modelAndView.addObject("client", userService.getHeaderClientData(clientId));
                return modelAndView.addObject("errorMessage", e.getMessage());
            }
            return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId);
        }
    }

    @GetMapping(path = "/{managerId}/users/filter")
    ModelAndView getUsersByFilter(@PathVariable("managerId") Long managerId,
                                  @ModelAttribute("filterCriteria") UserSearchCriteria userFilterCriteria,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        ModelAndView modelAndView = null;
        if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.MANAGER)) {
            modelAndView = new ModelAndView("manager/users");
        } else if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.ADMIN)) {
            modelAndView = new ModelAndView("admin/users");
        } else {
            throw new SecurityException("User does not have enough permission for this action");
        }
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        Page<UserDto> usersPage = userService.findUsersWithFilters(page, size, userFilterCriteria);
        modelAndView.addObject("users", usersPage);
        modelAndView.addObject("currentPage", usersPage.getNumber());
        modelAndView.addObject("totalPages", usersPage.getTotalPages());
        modelAndView.addObject("filterCriteria", new UserSearchCriteria());
        //TODO exceptions with page number
        return modelAndView;
    }


    @GetMapping(path = "/{managerId}/plans")
    ModelAndView getPlansModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/plans");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/plans/{planId}")
    ModelAndView getPlanModerationMenu(@PathVariable("managerId") Long managerId, @PathVariable("planId") Long planId) {
        ModelAndView modelAndView = new ModelAndView("manager/plan");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }


    @GetMapping(path = "/{managerId}/plans/create")
    ModelAndView createPlanModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_plan");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{userId}/plan/update")
    ModelAndView updatePlanStatus(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId, @RequestParam("status") String status) {
        log.info("Update user plan. User id: {}", userId);
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(status));
        return new ModelAndView("redirect:/managers/" + managerId + "/users/" + userId);
    }

    @GetMapping(path = "/{managerId}/emails")
    ModelAndView getEmailsMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("users", userService.findAllDtos());
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/emails/send")
    ModelAndView sendEmail(@PathVariable("managerId") Long managerId,
                           @Valid @ModelAttribute("email") EmailContext email) {
        log.info("Start sending email. send for user with email: {}", email.getTo());
        emailService.sendEmailWithAttachment(email);
        return new ModelAndView("redirect:/managers/" + managerId + "/emails");
    }

    @PostMapping(path = "/{managerId}/emails/send/clients/all")
    ModelAndView sendEmailsForAllClients(@PathVariable("managerId") Long managerId,
                                         @Valid @ModelAttribute("email") EmailContext email) {
        log.info("Start sending email. send for all clients with email");
        userService.sendEmailForAllClients(email);
        return new ModelAndView("redirect:/managers/" + managerId + "/emails");
    }


    @GetMapping(path = "/{userId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "userId") Long clientId) {
        UserHeaderDto manager = userService.getHeaderClientData(clientId);
        if (manager == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(clientId));
        modelAndView.addObject("manager", manager);
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(clientId));
        return modelAndView;
    }

    @GetMapping({"/{userId}/bank_cards/save", "/{userId}/bank_cards/{bankCardId}/save"})
    public ModelAndView clientBankCardsForm(@PathVariable Long userId, @PathVariable(required = false) Long bankCardId) {
        ModelAndView modelAndView = new ModelAndView("manager/add_bank_card");
        modelAndView.addObject("manager", userService.getHeaderClientData(userId));
        modelAndView.addObject("managerId", userService.getUserById(userId).getId());
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

    @PostMapping("/{managerId}/bank_cards")
    public ModelAndView saveBankCardToClient(@PathVariable Long managerId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("manager/add_bank_card")
                    .addObject("manager", userService.getHeaderClientData(managerId));
        }
        if (!bankCardService.isClientHasCard(managerId, bankCardDto)) {
            try {
                BankCard bankCard = bankCardService.save(bankCardDto);
                savingService.assignBankCardToClient(managerId, bankCard);
            } catch (IllegalArgumentException e) {
                return new ModelAndView("redirect:/managers/" + managerId + "/premium");
            }
        }
        return new ModelAndView("redirect:/managers/" + managerId);
    }

    @GetMapping("/{userId}/bank_cards/update")
    public ModelAndView updateBankCardForm(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("manager/update_delete_bank_card");
        UserHeaderDto userHeaderDto = userService.getHeaderClientData(userId);
        modelAndView.addObject("manager", userHeaderDto);
        modelAndView.addObject("bank_cards", userService.getUserById(userId).getSaving().getBankCards());
        return modelAndView;
    }

    @PutMapping("/{userId}/bank_cards")
    public ModelAndView updateBankCardData(@PathVariable Long userId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        return new ModelAndView("redirect:/managers/" + userId);
    }

    @GetMapping("/{userId}/bank_cards/{bankCardId}/remove")
    public ModelAndView removeBankCard(@PathVariable Long userId, @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ModelAndView("redirect:/managers/" + userId);
    }

    @GetMapping("/{userId}/income_expense_categories")
    public ModelAndView viewIncomeAndExpensesDashboard(@PathVariable Long userId) {
        ModelAndView modelAndView = new ModelAndView("manager/categories");
        modelAndView.addObject("manager", userService.getHeaderClientData(userId));//TODO Optimise maybe (it will be a lot of dto)
        modelAndView.addObject("incomes_categories", transactionService.findClientIncomeCategoriesByClientId(userId));
        modelAndView.addObject("expenses_categories", transactionService.findClientExpenseCategoriesByClientId(userId));
        return modelAndView;
    }

    @GetMapping("/{userId}/categories/{categoryName}")
    public ModelAndView viewTransactionsByCategoryName(@PathVariable Long userId,
                                                       @PathVariable String categoryName,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        ModelAndView modelAndView = new ModelAndView("manager/transactions_details");
        modelAndView.addObject("manager", userService.getHeaderClientData(userId));
        modelAndView.addObject("category", categoryService.findByName(categoryName));
        Page<TransactionDto> transactionPage = transactionService.findTransactionsByClientIdAndCategoryName(userId, categoryName, page, size);
        modelAndView.addObject("currentPage", transactionPage.getNumber());
        modelAndView.addObject("totalPages", transactionPage.getTotalPages());
        modelAndView.addObject("transactions", transactionPage);
        //TODO exceptions with page number
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/emails/search")
    public ModelAndView searchEmails(@PathVariable("managerId") Long managerId, @RequestParam("pattern") String pattern) {
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("users", userService.getUserListByEmailPattern(pattern));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/transactions")
    ModelAndView createTransactionForm(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_transaction");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("incomes", categoryService.findAllIncomeCategories());
        modelAndView.addObject("expenses", categoryService.findAllExpensesCategories());
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(managerId));
        modelAndView.addObject("transaction", new CreateTransactionDto());
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/transactions/save")
    ModelAndView saveTransaction(@PathVariable("managerId") Long managerId, @ModelAttribute CreateTransactionDto transaction) {
        userService.addTransaction(managerId, transaction);
        return new ModelAndView("redirect:/managers/" + managerId);
    }

    @GetMapping(path = "/{managerId}/categories")
    ModelAndView createNewCategoryForm(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_category");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("category", new CreateCategoryDto());
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/categories/create")
    ModelAndView createNewCategory(@PathVariable("managerId") Long managerId,
                                   @ModelAttribute("category") CreateCategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return new ModelAndView("redirect:/managers/" + managerId);
    }

    @GetMapping(path = "/{managerId}/categories/table")
    ModelAndView GetCategoriesTable(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/category_table");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("categories", categoryService.findAllDtos());
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/categories/{categoryId}/update")
    ModelAndView updateCategoryForm(@PathVariable("managerId") Long managerId,
                                    @PathVariable("categoryId") Long categoryId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_category");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("category", categoryService.findById(categoryId));
        return modelAndView;
    }

    @GetMapping(path = "/{userId}/premium/plan/{subscriptionStatus}")
    ModelAndView updateSubscriptionPlan(@PathVariable("userId") Long userId, @PathVariable("subscriptionStatus") String subscriptionStatus) {
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(subscriptionStatus));
        return new ModelAndView("redirect:/managers/" + userId+"/premium");
    }
}
