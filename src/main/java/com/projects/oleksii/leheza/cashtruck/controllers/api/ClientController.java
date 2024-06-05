package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admins", description = "Methods related to admins")
public class ClientController {

    private final UserService userService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final EmailService emailService;

    @PostMapping()
    public ResponseEntity<User> registerNewClient(@RequestBody CreateUserDto createUserDto) {
        User user = userService.saveClient(createUserDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable(value = "userId") Long clientId) {
        return new ResponseEntity<>(userService.getUserById(clientId), HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}/bank_cards")
    public ResponseEntity<List<BankCard>> getUserBankCards(@PathVariable(value = "userId") Long clientId) {
        return new ResponseEntity<>(userService.getBankCardsByUserId(clientId), HttpStatus.OK);
    }

    @GetMapping(path = "/bank_cards/{bankCardId}")
    public ResponseEntity<BankCard> getBankCardById(@PathVariable(value = "bankCardId") Long bankCardId) {
        return new ResponseEntity<>(bankCardService.getById(bankCardId), HttpStatus.OK);
    }

    @PostMapping(path = "/{userId}/bank_cards")
    public ResponseEntity<Boolean> saveBankCardToUser(@PathVariable Long userId,
                                                      @Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(userId, bankCard);
        return new ResponseEntity<>(bankCardService.isClientHasCard(userId, bankCardDto), HttpStatus.OK);
    }

    @PutMapping(path = "/bank_cards")
    public ResponseEntity<BankCard> updateBankCard(@Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        return new ResponseEntity<>(bankCard, HttpStatus.OK);
    }

    @DeleteMapping("{userId}/bank_cards/{bankCardId}/remove")
    public ResponseEntity<Void> removeBankCard(@PathVariable Long userId,
                                               @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/categories/incomes")
    public ResponseEntity<List<CategoryInfoDto>> getIncomeCategoriesByClientId(@PathVariable Long userId) {
        return new ResponseEntity<>(transactionService.findClientIncomeCategoriesByClientId(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/categories/expenses")
    public ResponseEntity<List<CategoryInfoDto>> getExpenseCategoriesByClientId(@PathVariable Long userId) {
        return new ResponseEntity<>(transactionService.findClientExpenseCategoriesByClientId(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/categories/{categoryName}")
    public ResponseEntity<PageDto<TransactionDto>> getClientTransactionsByCategoryName(@PathVariable Long userId,
                                                                                       @PathVariable String categoryName,
                                                                                       @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageDto<TransactionDto> transactionPage = transactionService.findTransactionsByClientIdAndCategoryName(userId, categoryName, pageNumber, pageSize);
        return new ResponseEntity<>(transactionPage, transactionPage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUserAccount(@PathVariable Long userId,
                                                     @Valid @RequestBody UserUpdateDto userUpdateDto,
                                                     @RequestBody MultipartFile avatar) {
        UserDto user = userService.updateUserInfo(userId, userUpdateDto);
        userService.updateAvatar(userId, avatar);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(path = "/emails")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailContext email) {
        emailService.sendEmailWithAttachment(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
