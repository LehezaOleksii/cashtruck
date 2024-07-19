package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DtoMapper {

    private final ImageConvertor imageConvertor;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

    public TransactionDto transactionToDto(Transaction transaction) {
        return TransactionDto.builder()
                .sum(transaction.getBankTransaction().getSum())
                .name(transaction.getBankTransaction().getName())
                .time(transaction.getBankTransaction().getTime())
                .category(transaction.getCategory().getName())
                .transactionType(transaction.getCategory().getTransactionType().toString())
                .build();
    }

    public CategoryInfoDto categoryToDtoInfo(List<TransactionDto> transactionDtos, Category category) {
        String categoryName = category.getName();
        double totalSum = transactionDtos
                .stream()
                .mapToDouble(transactionDto -> transactionDto.getSum().doubleValue())
                .sum();
        double totalSumByCategory = transactionDtos.stream()
                .filter(transactionDto -> transactionDto.getCategory().equals(categoryName))
                .mapToDouble(transactionDto -> transactionDto.getSum().doubleValue())
                .sum();
        double categoryPercentage = (totalSum == 0) ? 0 : (totalSumByCategory / totalSum * 100);
        return CategoryInfoDto.builder()
                .name(categoryName)
                .categoryPercentage(categoryPercentage)
                .fullCategoryTransactionSum(new BigDecimal(totalSumByCategory))
                .build();
    }

    public CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .transactionType(category.getTransactionType())
                .name(category.getName())
                .build();
    }

    public CreateCategoryDto categoryToCreateDto(Category category) {
        return CreateCategoryDto.builder()
                .id(category.getId())
                .transactionType(category.getTransactionType())
                .categoryName(category.getName())
                .build();
    }

    public UserDto userToDto(User user) {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bankCards(user.getBankCards())
                .expenses(user.getBankCards().stream()
                        .flatMap(bankCard -> bankCard.getTransactions().stream())
                        .filter(transaction -> transaction.getCategory().getTransactionType().equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .incomes(user.getBankCards().stream()
                        .flatMap(bankCard -> bankCard.getTransactions().stream())
                        .filter(transaction -> transaction.getCategory().getTransactionType().equals(TransactionType.INCOME))
                        .collect(Collectors.toList()))
                .role(String.valueOf(user.getRole()))
                .status(String.valueOf(user.getStatus()))
                .subscription(user.getSubscription().getSubscriptionStatus().toString())
                .subscriptionPrice(user.getSubscription().getPrice())
                .subscriptionFinishDate(user.getSubscriptionFinishDate())
                .build();
        if (user.getAvatar() != null) {
            dto.setAvatar(imageConvertor.convertByteImageToString(user.getAvatar().getImageBytes()));
        }
        return dto;
    }

    public UserUpdateDto clientToClientUpdateDto(User user) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        Image image = user.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userUpdateDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userUpdateDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userUpdateDto.toBuilder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        Image image = user.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userDto.toBuilder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole().toString())
                .status(String.valueOf(user.getStatus()))
                .build();
    }

    public BankTransaction transactionDtoToTransaction(CreateTransactionDto transactionDto) {
        return BankTransaction.builder()
                .name(transactionDto.getTransactionName())
                .time(LocalDateTime.parse(transactionDto.getTime()))
                .sum(transactionDto.getSum())
                .build();
    }

    public Category categoryDtoToCategory(CreateCategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getCategoryName())
                .transactionType(categoryDto.getTransactionType())
                .build();
    }

    public PaymentCreateRequest subscriptionToPaymentRequest(Subscription subscription) {
        return PaymentCreateRequest.builder()
                .price(subscription.getPrice().longValue())
                .subscriptionPlan(subscription.getSubscriptionStatus().name())
                .build();
    }

    public BankCard bankCardDtoToBankCard(BankCardDto bankCardDto) {
        return BankCard.builder()
                .id(bankCardDto.getId())
                .cvv(bankCardDto.getCvv())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .cardHolder(bankCardDto.getCardHolder())
                .expiringDate(bankCardDto.getExpiringDate())
                .balance(BigDecimal.valueOf(bankCardDto.getBalance()))
                .build();
    }

    public BankCardDto bankCardToBankCardDto(BankCard bankCard) {
        return BankCardDto.builder()
                .id(bankCard.getId())
                .cvv(bankCard.getCvv())
                .bankName(bankCard.getBankName())
                .cardNumber(bankCard.getCardNumber())
                .cardHolder(bankCard.getCardHolder())
                .expiringDate(bankCard.getExpiringDate())
                .balance(bankCard.getBalance().doubleValue())
                .build();
    }
}
