package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
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
import com.projects.oleksii.leheza.cashtruck.exception.UserPlanException;
import com.projects.oleksii.leheza.cashtruck.service.email.EmailServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
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

    @PutMapping(path = "/update")
    ModelAndView updateManagerInfo(@ModelAttribute("manager") UserUpdateDto userUpdateDto,
                                   @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView();
        log.info("Start Updating manager info. User email: {}", userUpdateDto.getEmail());
        userService.updateUserInfo(userId, userUpdateDto);
        return modelAndView;
    }

//    @GetMapping(path = "/{managerId}")
//    ModelAndView showManagerDashboard(@PathVariable("managerId") Long managerId) {
//        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
//        modelAndView.addObject("managerId", managerId);
////        modelAndView.addObject("manager", userService.(managerId));
//        return modelAndView;
//    }

    @GetMapping(path = "/users")
    ModelAndView getClientsList(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView;
        if (Role.valueOf(userService.getUserById(userId).getRole()) == (Role.ROLE_MANAGER)) {
            modelAndView = new ModelAndView("manager/users");
        } else if (Role.valueOf(userService.getUserById(userId).getRole()) == (Role.ROLE_ADMIN)) {
            modelAndView = new ModelAndView("admin/users");
        } else {
            throw new SecurityException("User does not have enough permission for this action");
        }
        modelAndView.addObject("managerId", userId);
        modelAndView.addObject("manager", userService.getUserDto(userId));
        Page<UserDto> usersPage = userService.findAll(page, size);
        modelAndView.addObject("users", usersPage.getContent());
        modelAndView.addObject("currentPage", usersPage.getNumber());
        modelAndView.addObject("totalPages", usersPage.getTotalPages());
        modelAndView.addObject("filterCriteria", new UserSearchCriteria());
        return modelAndView;
    }

    @GetMapping(path = "/users/{userId}")
    ModelAndView getClientById(@PathVariable("userId") Long userId,
                               @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView;
        if (userService.getUserById(userId).getRole().equals((Role.ROLE_CLIENT.toString()))) {
            modelAndView = new ModelAndView("manager/client_info");
        } else {
            modelAndView = new ModelAndView("manager/manager_info");
        }
        modelAndView.addObject("user", userService.getUserById(userId));
        modelAndView.addObject("manager", userService.getUserById(managerId));
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(userId));
        return modelAndView;
    }

    @GetMapping(path = "/users/{userId}/update/form")
    ModelAndView updateClientFormById(@PathVariable("userId") Long userId,
                                      @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/client_info_edit");
        modelAndView.addObject("user", userService.getUserById(userId));
        modelAndView.addObject("manager", userService.getUserById(managerId));
        UserUpdateDto userUpdateDto = userService.getClientUpdateDto(userId);
        modelAndView.addObject("clientDto", userUpdateDto);
        modelAndView.addObject("statuses", ActiveStatus.values());
        return modelAndView;
    }


    @PostMapping("/users/{userId}/update")
    public ModelAndView updateClientAccount(@PathVariable Long userId,
                                            @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto,
                                            @AuthenticationPrincipal User user,
                                            BindingResult bindingResult) {
        Long managerId = user.getId();
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
            return new ModelAndView("redirect:/managers/users/" + userId);
        }
    }

    @GetMapping(path = "/users/{userId}/block")
    RedirectView blockUser(@PathVariable("userId") Long userId,
                           @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        log.info("Start blocking user. User id: {}", userId);
        userService.blockUser(userId);
        return new RedirectView("/managers/users/" + userId);
    }

    @GetMapping(path = "/users/{userId}/unblock")
    RedirectView unblockUser(@PathVariable("userId") Long userId,
                             @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        log.info("Start unblocking user. User id: {}", userId);
        userService.unblockUser(userId);
        return new RedirectView("/managers/users");
    }

    @GetMapping(path = "/users/{userId}/profile")
    ModelAndView getClientProfileForm(@PathVariable("userId") Long userId,
                                      @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView;
        if (userService.getUserById(userId).getRole().equals((Role.ROLE_CLIENT.toString()))) {
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

    @PostMapping(path = "/users/{clientId}/profile")
    ModelAndView changeClientProfile(@PathVariable("clientId") Long clientId,
                                     @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto,
                                     BindingResult bindingResult,
                                     @RequestParam("image") MultipartFile avatar,
                                     @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
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
                    return new ModelAndView("redirect:/managers/users/" + clientId)
                            .addObject("errorMessage", e.getMessage());
                }
            }
            try {
                userService.updateUserInfo(clientId, userUpdateDto);
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:/managers/users/" + clientId + "/profile");
                modelAndView.addObject("client", userService.getHeaderClientData(clientId));
                return modelAndView.addObject("errorMessage", e.getMessage());
            }
            return new ModelAndView("redirect:/managers/users/" + clientId);
        }
    }

    @GetMapping(path = "/users/filter")
    ModelAndView getUsersByFilter(@ModelAttribute("filterCriteria") UserSearchCriteria userFilterCriteria,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                  @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = null;
        if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.ROLE_MANAGER)) {
            modelAndView = new ModelAndView("manager/users");
        } else if (Role.valueOf(userService.getUserById(managerId).getRole()) == (Role.ROLE_ADMIN)) {
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

//    @GetMapping(path = "/plans")
//    ModelAndView getPlansModerationMenu(@AuthenticationPrincipal User user) {
//        Long userId = user.getId();
//        ModelAndView modelAndView = new ModelAndView("manager/plans");
//        modelAndView.addObject("manager", userService.getUserDto(managerId));
//        return modelAndView;
//    }
//
//    @GetMapping(path = "/{managerId}/plans/{planId}")
//    ModelAndView getPlanModerationMenu(@PathVariable("managerId") Long managerId,
//                                       @PathVariable("planId") Long planId) {
//        ModelAndView modelAndView = new ModelAndView("manager/plan");
//        modelAndView.addObject("manager", userService.getUserDto(managerId));
//        return modelAndView;
//    }


//    @GetMapping(path = "/plans/create")
//    ModelAndView createPlanModerationMenu(@AuthenticationPrincipal User user) {
//        Long managerId = user.getId();
//        ModelAndView modelAndView = new ModelAndView("manager/create_plan");
//        modelAndView.addObject("manager", userService.getUserDto(managerId));
//        return modelAndView;
//    }

    @GetMapping(path = "/users/{userId}/plan/update")
    ModelAndView updatePlanStatus(@PathVariable("userId") Long userId,
                                  @RequestParam("status") String status,
                                  @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        log.info("Update user plan. User id: {}", userId);
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(status));
        return new ModelAndView("redirect:/managers/users/" + userId);
    }

    @GetMapping(path = "/emails")
    ModelAndView getEmailsMenu(@AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("users", userService.findAllDtos());
        return modelAndView;
    }

    @PostMapping(path = "/emails/send")
    ModelAndView sendEmail(@Valid @ModelAttribute("email") EmailContext email,
                           @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        log.info("Start sending email. send for user with email: {}", email.getTo());
        emailService.sendEmailWithAttachment(email);
        return new ModelAndView("redirect:/managers/emails");
    }

    @PostMapping(path = "/emails/send/clients/all")
    ModelAndView sendEmailsForAllClients(@Valid @ModelAttribute("email") EmailContext email,
                                         @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        log.info("Start sending email. send for all clients with email");
        userService.sendEmailForAllClients(email);
        return new ModelAndView("redirect:/managers/emails");
    }


    @GetMapping(path = "/dashboard")
    public ModelAndView showClientDashboard(@AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        UserHeaderDto manager = userService.getHeaderClientData(managerId);
        if (manager == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(managerId));
        modelAndView.addObject("manager", manager);
        modelAndView.addObject("client_statistic", userService.getClientStatisticByUserId(managerId));
        return modelAndView;
    }

    @GetMapping({"/bank_cards"})
    public ModelAndView clientBankCardsForm(@RequestParam(required = false) Long bankCardId,
                                            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (bankCardId != null && !bankCardService.isClientHasCard(userId, bankCardId)) {
            throw new SecurityException("user has not card with id:" + bankCardId);
        }
        ModelAndView modelAndView = new ModelAndView("manager/add_bank_card");
        modelAndView.addObject("manager", userService.getHeaderClientData(userId));//TODO use DTO
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

    @PostMapping("/bank_cards/save")
    public ModelAndView saveBankCardToClient(@Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto,
                                             @AuthenticationPrincipal User user,
                                             BindingResult bindingResult) {
        Long userId = user.getId();
        if (bindingResult.hasFieldErrors()) {
            log.warn("validation problems were occurring at the save bank card process. userId:{} ,bank card number{}", userId, bankCardDto.getCardNumber());
            return new ModelAndView("manager/add_bank_card")
                    .addObject("manager", userService.getHeaderClientData(userId));
        }
        if (!bankCardService.isClientHasCard(userId, bankCardDto)) {
            try {
                BankCard bankCard = bankCardService.save(bankCardDto);
                savingService.assignBankCardToClient(userId, bankCard);
            } catch (UserPlanException ex) {
                log.warn("user with id: {} has bank card with number:{}", userId, bankCardDto.getCardNumber());
                return new ModelAndView("redirect:/managers/premium");
            }
        }
        return new ModelAndView("redirect:/managers/dashboard");
    }

    @GetMapping("/bank_cards/update")
    public ModelAndView updateBankCardForm(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/update_delete_bank_card");
        UserHeaderDto userHeaderDto = userService.getHeaderClientData(userId);
        modelAndView.addObject("manager", userHeaderDto);
        modelAndView.addObject("bank_cards", userService.getUserById(userId).getSaving().getBankCards());
        return modelAndView;
    }

    @PutMapping("/bank_cards")
    public ModelAndView updateBankCardData(@Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        return new ModelAndView("redirect:/managers/dashboard");
    }

    @GetMapping("/bank_cards/remove")
    public ModelAndView removeBankCard(@RequestParam Long bankCardId,
                                       @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if (!bankCardService.isClientHasCard(userId, bankCardId)) {
            throw new java.lang.SecurityException("user has not card with id:" + bankCardId);
        }
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ModelAndView("redirect:/managers/dashboard");
    }

    @GetMapping("/income_expense_categories")
    public ModelAndView viewIncomeAndExpensesDashboard(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/categories");
        modelAndView.addObject("manager", userService.getHeaderClientData(userId));//TODO Optimise maybe (it will be a lot of dto)
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

    @GetMapping(path = "/emails/search")
    public ModelAndView searchEmails(@RequestParam("pattern") String pattern,
                                     @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        modelAndView.addObject("users", userService.getUserListByEmailPattern(pattern));
        return modelAndView;
    }

    @GetMapping(path = "/transactions")
    ModelAndView createTransactionForm(@AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/create_transaction");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("incomes", categoryService.findAllIncomeCategories());
        modelAndView.addObject("expenses", categoryService.findAllExpensesCategories());
        modelAndView.addObject("bank_cards", userService.getBankCardsByUserId(managerId));
        modelAndView.addObject("transaction", new CreateTransactionDto());
        return modelAndView;
    }

    @PostMapping(path = "/transactions/save")
    ModelAndView saveTransaction(@ModelAttribute CreateTransactionDto transaction,
                                 @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        userService.addTransaction(managerId, transaction);
        return new ModelAndView("redirect:/managers/dashboard");
    }

    @GetMapping(path = "/categories")
    ModelAndView createNewCategoryForm(@AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/create_category");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("category", new CreateCategoryDto());
        return modelAndView;
    }

    @PostMapping(path = "/categories/create")
    ModelAndView createNewCategory(@ModelAttribute("category") CreateCategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return new ModelAndView("redirect:/managers/dashboard");
    }

    @GetMapping(path = "/categories/table")
    ModelAndView GetCategoriesTable(@AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/category_table");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("categories", categoryService.findAllDtos());
        return modelAndView;
    }

    @GetMapping(path = "/categories/{categoryId}/update")
    ModelAndView updateCategoryForm(@PathVariable("categoryId") Long categoryId,
                                    @AuthenticationPrincipal User user) {
        Long managerId = user.getId();
        ModelAndView modelAndView = new ModelAndView("manager/create_category");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("category", categoryService.findById(categoryId));
        return modelAndView;
    }

    @GetMapping(path = "/premium/plan/{subscriptionStatus}")
    ModelAndView updateSubscriptionPlan(@PathVariable("subscriptionStatus") String subscriptionStatus,
                                        @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userService.updateUserPlan(userId, SubscriptionStatus.valueOf(subscriptionStatus));
        return new ModelAndView("redirect:/managers/premium");
    }
}
