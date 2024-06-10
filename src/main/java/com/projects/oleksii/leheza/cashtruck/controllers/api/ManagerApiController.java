package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.ErrorResponse;
import com.projects.oleksii.leheza.cashtruck.exception.ImageException;
import com.projects.oleksii.leheza.cashtruck.service.email.EmailServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class ManagerApiController {

    private final UserService userService;
    private final ImageConvertor imageConvertor;
    private final EmailServiceImpl emailService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @Operation(summary = "Update user", description = "Update an existing user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/{userId}")
    public ResponseEntity<UserDto> updateManagerInfo(@PathVariable("userId") Long managerId,
                                                     @RequestBody UserUpdateDto userUpdateDto) {
        UserDto user = userService.updateUserInfo(managerId, userUpdateDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Get user", description = "Get page of users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Users are not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/users")
    public ResponseEntity<PageDto<UserDto>> getUsersPage(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageDto<UserDto> usersPage = userService.findAll(pageNumber, pageSize);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
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
    @GetMapping(path = "/users/{userId}")
    public ResponseEntity<UserDto> getClientById(@PathVariable("userId") Long userId) {
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
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
    @GetMapping(path = "/users/{userId}/bank_cards")
    public ResponseEntity<List<BankCard>> getClientBankCardsByClientId(@PathVariable("userId") Long userId) {
        List<BankCard> bankCards = userService.getBankCardsByUserId(userId);
        return new ResponseEntity<>(bankCards, bankCards != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Block user", description = "Set block status for user account by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User blocked successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/users/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable("userId") Long userId) {
        userService.blockUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Unblock user", description = "Set unblock status for user account by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unblocked successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/users/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable("userId") Long userId) {
        userService.unblockUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update user", description = "Update an existing user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User duplicate",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/users/{clientId}")
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

    @Operation(summary = "Get user page", description = "Get user page by filter criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Users is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/users/filter")
    public ResponseEntity<Page<UserDto>> getUsersByFilter(@RequestBody UserSearchCriteria userFilterCriteria,
                                                          @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<UserDto> usersPage = userService.findUsersWithFilters(pageNumber, pageSize, userFilterCriteria);
        return new ResponseEntity<>(usersPage, usersPage != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update subscription status for a user", description = "Update subscription status for a user by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription status updated successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscription status is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/users/{userId}/plan")
    public ResponseEntity<SubscriptionStatus> updatePlanStatus(@PathVariable("userId") Long userId,
                                                               @RequestParam("status") String status) {
        SubscriptionStatus subscriptionStatus = userService.updateUserPlan(userId, SubscriptionStatus.valueOf(status));
        return new ResponseEntity<>(subscriptionStatus, HttpStatus.OK);
    }

    @Operation(summary = "Send a new email", description = "Send a new email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/emails")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailContext email) {
        emailService.sendEmailWithAttachment(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Send an email for all clients", description = "Send an email for all clients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent for users successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/emails/send/clients/all")
    public ResponseEntity<Void> sendEmailsForAllClients(@Valid @RequestBody EmailContext email) {
        userService.sendEmailForAllClients(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get user statistic", description = "Get user statistic dto by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users statistic found successfully",
                    content = @Content(schema = @Schema(implementation = ClientStatisticDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Users statistic is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/{userId}")
    public ResponseEntity<ClientStatisticDto> getClientStatistic(@PathVariable(value = "userId") Long clientId) {
        ClientStatisticDto statisticDto = userService.getClientStatisticByUserId(clientId);
        return new ResponseEntity<>(statisticDto, HttpStatus.OK);
    }

    @Operation(summary = "Create bank card and save to client", description = "Create bank card and save to client by it`s id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bank card created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/users/{userId}/bank_cards")
    public ResponseEntity<Void> saveBankCardToClient(@PathVariable Long userId,
                                                     @Valid @RequestBody CreateBankCardDto bankCardDto) {
        BankCard bankCard = bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(userId, bankCard);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update bank card", description = "Update an existing bank card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank card updated successfully",
                    content = @Content(schema = @Schema(implementation = BankCard.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bank card is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/bank_cards")
    public ResponseEntity<BankCard> updateBankCardData(@Valid @RequestBody CreateBankCardDto bankCardDto) {
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
    @DeleteMapping("/{userId}/bank_cards/{bankCardId}")
    public ResponseEntity<Void> removeBankCard(@PathVariable Long userId,
                                               @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get users by email", description = "Get users by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users by email found successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Users are not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/emails/search")
    public ResponseEntity<PageDto<UserDto>> searchEmails(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam("pattern") String pattern) {
        PageDto<UserDto> page = userService.getUserPageByEmailPattern(pattern, pageNumber, pageSize);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Operation(summary = "Get all categories", description = "Get all income and expenses categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories found successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/categories")
    public ResponseEntity<PageDto<CategoryDto>> saveCategories(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(categoryService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @Operation(summary = "Create new category", description = "Create new category from create category dto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/categories")
    public ResponseEntity<Category> getCategories(@RequestBody CreateCategoryDto category) {
        return new ResponseEntity<>(categoryService.save(category), HttpStatus.CREATED);
    }

    @Operation(summary = "Update new category", description = "Update new category from create category dto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category update successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/categories")
    public ResponseEntity<Category> updateCategory(@RequestBody CreateCategoryDto category) {
        return new ResponseEntity<>(categoryService.save(category), HttpStatus.OK);
    }
}
