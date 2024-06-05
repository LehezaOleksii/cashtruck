package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.ImageException;
import com.projects.oleksii.leheza.cashtruck.service.email.EmailServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Managers", description = "Methods related to managers")
public class ManagerController {

    private final UserService userService;
    private final ImageConvertor imageConvertor;
    private final EmailServiceImpl emailService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @PutMapping(path = "/{userId}/update/")
    public ResponseEntity<UserDto> updateManagerInfo(@PathVariable("userId") Long managerId,
                                                     @RequestBody UserUpdateDto userUpdateDto) {
        UserDto user = userService.updateUserInfo(managerId, userUpdateDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/users")
    public ResponseEntity<PageDto<UserDto>> getUsersPage(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageDto<UserDto> usersPage = userService.findAll(pageNumber, pageSize);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @GetMapping(path = "/users/{userId}")
    public ResponseEntity<UserDto> getClientById(@PathVariable("userId") Long userId) {
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/users/{userId}")
    public ResponseEntity<List<BankCard>> getClientBankCardsByClientId(@PathVariable("userId") Long userId) {
        List<BankCard> bankCards = userService.getBankCardsByUserId(userId);
        return new ResponseEntity<>(bankCards, bankCards != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/users/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable("userId") Long userId) {
        userService.blockUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/users/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable("userId") Long userId) {
        userService.unblockUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{managerId}/users/{clientId}")
    public ResponseEntity<UserDto> updateClientProfile(@PathVariable("clientId") Long clientId,
                                                       @Valid @RequestBody UserUpdateDto userUpdateDto,
                                                       @RequestParam("image") MultipartFile avatar) {
        if (!avatar.isEmpty()) {
            try {
                userUpdateDto.setAvatar(imageConvertor.convertByteImageToString(avatar.getBytes()));
            } catch (IOException ex) {
                throw new ImageException(ex.getMessage());
            }
        }
        UserDto user = userService.updateUserInfo(clientId, userUpdateDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/users/filter")
    public ResponseEntity<Page<UserDto>> getUsersByFilter(@RequestBody UserSearchCriteria userFilterCriteria,
                                                          @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<UserDto> usersPage = userService.findUsersWithFilters(pageNumber, pageSize, userFilterCriteria);
        return new ResponseEntity<>(usersPage, usersPage != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/users/{userId}/plan")
    public ResponseEntity<SubscriptionStatus> updatePlanStatus(@PathVariable("userId") Long userId,
                                                               @RequestParam("status") String status) {
        SubscriptionStatus subscriptionStatus = userService.updateUserPlan(userId, SubscriptionStatus.valueOf(status));
        return new ResponseEntity<>(subscriptionStatus, HttpStatus.OK);
    }

    @PostMapping(path = "/emails")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailContext email) {
        emailService.sendEmailWithAttachment(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/emails/send/clients/all")
    public ResponseEntity<Void> sendEmailsForAllClients(@Valid @RequestBody EmailContext email) {
        userService.sendEmailForAllClients(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<ClientStatisticDto> getClientStatistic(@PathVariable(value = "userId") Long clientId) {
        ClientStatisticDto statisticDto = userService.getClientStatisticByUserId(clientId);
        return new ResponseEntity<>(statisticDto, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/bank_cards")
    public ResponseEntity<Void> saveBankCardToClient(@PathVariable Long userId,
                                                     @Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(userId, bankCard);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/bank_cards")
    public ResponseEntity<BankCard> updateBankCardData(@Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        return new ResponseEntity<>(bankCard, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/bank_cards/{bankCardId}")
    public ResponseEntity<Void> removeBankCard(@PathVariable Long userId,
                                               @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/emails/search")
    public ResponseEntity<PageDto<UserDto>> searchEmails(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam("pattern") String pattern) {
        PageDto<UserDto> page = userService.getUserPageByEmailPattern(pattern, pageNumber, pageSize);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
