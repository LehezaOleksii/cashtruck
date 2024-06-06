package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.Image;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DtoMapper {

    private final ImageConvertor imageConvertor;
    private final ImageService imageService;

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
                .transactionType(category.getTransactionType())
                .name(category.getName())
                .build();
    }

    public CreateUserDto clientToCreateDto(User user) {
        return CreateUserDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
//                .avatar(user.getAvatar().getBytes())
                .build();
    }

    public UserDto userToDto(User user) {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .language(user.getLanguage())
                .country(user.getCountry())
                .phoneNumber(user.getPhoneNumber())
                .saving(user.getSaving())
                .expenses(user.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .incomes(user.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
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

    public User dtoToClient(UserDto clientdto) {
        List<Transaction> transactions = clientdto.getIncomes();
        transactions.addAll(clientdto.getExpenses());
        return User.builder()
                .id(clientdto.getId())
                .firstName(clientdto.getFirstName())
                .lastName(clientdto.getLastName())
                .avatar(new Image(clientdto.getAvatar().getBytes()))
                .language(clientdto.getLanguage())
                .email(clientdto.getEmail())
                .password(clientdto.getPassword())
                .phoneNumber(clientdto.getPhoneNumber())
                .country(clientdto.getCountry())
                .saving(clientdto.getSaving())
                .transactions(transactions)
                .status(ActiveStatus.valueOf(clientdto.getStatus()))
                .build();
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
                .password(user.getPassword())
                .status(String.valueOf(user.getStatus()))
                .build();
    }

    public User updateClientDtoToClient(UserUpdateDto userUpdateDto) {
        return User.builder()
                .id(userUpdateDto.getId())
                .firstName(userUpdateDto.getFirstName())
                .lastName(userUpdateDto.getLastName())
                .avatar(new Image(imageConvertor.convertStringToByteImage(userUpdateDto.getAvatar())))
                .email(userUpdateDto.getEmail())
                .password(userUpdateDto.getPassword())
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
                .password(user.getPassword())
                .language(user.getLanguage())
                .country(user.getCountry())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().toString())
                .status(String.valueOf(user.getStatus()))
                .build();
    }

    public User userDtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .avatar(new Image(imageConvertor.convertStringToByteImage(userDto.getAvatar())))
                .language(userDto.getLanguage())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .country(userDto.getCountry())
                .build();
    }
}
