package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.exception.ErrorResponse;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Tag(name = "Admins", description = "Methods related to admins")
public class ClientApiController {

    private final UserService userService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final EmailService emailService;

    @Operation(summary = "Create a new user", description = "Create a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User email duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping()
    public ResponseEntity<User> registerNewClient(@RequestBody CreateUserDto createUserDto) {
        log.info("start register new user with email:{}", createUserDto.getEmail());
        User user = userService.saveClient(createUserDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @Operation(summary = "Find user by id", description = "Find a user by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable(value = "userId") Long clientId) {
        return new ResponseEntity<>(userService.getUserById(clientId), HttpStatus.OK);
    }

    @Operation(summary = "Find user`s bank cards by user id", description = "Find a list of user`s bank cards posts by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank cards retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/{userId}/bank_cards")
    public ResponseEntity<List<BankCard>> getUserBankCards(@PathVariable(value = "userId") Long clientId) {
        return new ResponseEntity<>(userService.getBankCardsByUserId(clientId), HttpStatus.OK);
    }

    @Operation(summary = "Find bank card by id", description = "Find bank card by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank card found successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bank card is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/bank_cards/{bankCardId}")
    public ResponseEntity<BankCard> getBankCardById(@PathVariable(value = "bankCardId") Long bankCardId) {
        return new ResponseEntity<>(bankCardService.getById(bankCardId), HttpStatus.OK);
    }

    @Operation(summary = "Create bank card and save to client", description = "Create bank card and save to client by it`s id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bank card created successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Bank card email duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/{userId}/bank_cards")
    public ResponseEntity<BankCard> saveBankCardToUser(@PathVariable Long userId,
                                                       @Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(userId, bankCard);
        return new ResponseEntity<>(bankCard, HttpStatus.OK);
    }

    @Operation(summary = "Update bank card", description = "Update an existing bank card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank card updated successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bank card is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Bank card duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/bank_cards")
    public ResponseEntity<BankCard> updateBankCard(@Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        return new ResponseEntity<>(bankCard, HttpStatus.OK);
    }

    @Operation(summary = "Delete a bank card by bank card id", description = "Delete a bank card by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bank card deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bank card is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("{userId}/bank_cards/{bankCardId}/remove")
    public ResponseEntity<Void> removeBankCard(@PathVariable Long userId,
                                               @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(summary = "Find user income categories", description = "Find user income categories by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Income found successfully",
                    content = @Content(schema = @Schema(implementation = CategoryInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No income found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{userId}/categories/incomes")
    public ResponseEntity<List<CategoryInfoDto>> getIncomeCategoriesByClientId(@PathVariable Long userId) {
        return new ResponseEntity<>(transactionService.findClientIncomeCategoriesByClientId(userId), HttpStatus.OK);
    }

    @Operation(summary = "Find user expense categories", description = "Find user expense categories by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense found successfully",
                    content = @Content(schema = @Schema(implementation = CategoryInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No expense found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{userId}/categories/expenses")
    public ResponseEntity<List<CategoryInfoDto>> getExpenseCategoriesByClientId(@PathVariable Long userId) {
        return new ResponseEntity<>(transactionService.findClientExpenseCategoriesByClientId(userId), HttpStatus.OK);
    }

    @Operation(summary = "Get user transactions by category name", description = "Get user transactions by category name and user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transactions is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{userId}/categories/{categoryName}")
    public ResponseEntity<PageDto<TransactionDto>> getClientTransactionsByCategoryName(@PathVariable Long userId,
                                                                                       @PathVariable String categoryName,
                                                                                       @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageDto<TransactionDto> transactionPage = transactionService.findTransactionsByClientIdAndCategoryName(userId, categoryName, pageNumber, pageSize);
        return new ResponseEntity<>(transactionPage, transactionPage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update user", description = "Update an existing user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUserAccount(@PathVariable Long userId,
                                                     @Valid @RequestBody UserUpdateDto userUpdateDto,
                                                     @RequestBody MultipartFile avatar) {
        UserDto user = userService.updateUserInfo(userId, userUpdateDto);
        userService.updateAvatar(userId, avatar);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Send email", description = "Send email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Email sent successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/emails")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailContext email) {
        log.info("start sending email for user with email:{}", email.getEmail());
        emailService.sendEmailWithAttachment(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Add transaction to application", description = "Add transaction data to the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "{userId}/transactions")
    public ResponseEntity<TransactionDto> saveTransaction(@PathVariable Long userId,
                                                          @Valid @RequestBody CreateTransactionDto transaction) {
        userService.addTransaction(userId, transaction);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
