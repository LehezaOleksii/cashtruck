package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.domain.Image;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ImageService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
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

    public CreateUserDto clientToCreateDto(CustomUser customUser) {
        return CreateUserDto.builder()
                .email(customUser.getEmail())
                .password(customUser.getPassword())
                .firstName(customUser.getFirstName())
                .lastName(customUser.getLastName())
//                .avatar(user.getAvatar().getBytes())
                .build();
    }

    public UserDto userToDto(CustomUser customUser) {
        UserDto dto = UserDto.builder()
                .id(customUser.getId())
                .email(customUser.getEmail())
                .password(customUser.getPassword())
                .firstName(customUser.getFirstName())
                .lastName(customUser.getLastName())
                .language(customUser.getLanguage())
                .country(customUser.getCountry())
                .phoneNumber(customUser.getPhoneNumber())
                .saving(customUser.getSaving())
                .expenses(customUser.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .incomes(customUser.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .build();
        if (customUser.getAvatar() != null) {
            dto.setAvatar(imageConvertor.convertByteImageToString(customUser.getAvatar().getImageBytes()));
        }
        return dto;
    }

    public CustomUser dtoToClient(UserDto clientdto) {
        List<Transaction> transactions = clientdto.getIncomes();
        transactions.addAll(clientdto.getExpenses());
        return CustomUser.builder()
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
                .roles(Collections.singletonList(Role.CLIENT))
                .build();
    }

    public UserUpdateDto clientToClientUpdateDto(CustomUser customUser) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        Image image = customUser.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userUpdateDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userUpdateDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userUpdateDto.toBuilder()
                .id(customUser.getId())
                .firstName(customUser.getFirstName())
                .lastName(customUser.getLastName())
                .email(customUser.getEmail())
                .password(customUser.getPassword())
                .language(customUser.getLanguage())
                .country(customUser.getCountry())
                .phoneNumber(customUser.getPhoneNumber())
                .build();
    }

    public CustomUser updateClientDtoToClient(UserUpdateDto userUpdateDto) {
        return CustomUser.builder()
                .id(userUpdateDto.getId())
                .firstName(userUpdateDto.getFirstName())
                .lastName(userUpdateDto.getLastName())
                .avatar(new Image(imageConvertor.convertStringToByteImage(userUpdateDto.getAvatar())))
                .language(userUpdateDto.getLanguage())
                .email(userUpdateDto.getEmail())
                .password(userUpdateDto.getPassword())
                .phoneNumber(userUpdateDto.getPhoneNumber())
                .country(userUpdateDto.getCountry())
                .roles(Collections.singletonList(Role.CLIENT))
                .build();
    }

    public UserDto userToUserDto(CustomUser customUser) {
        UserDto userDto = new UserDto();
        Image image = customUser.getAvatar();
        if (image != null && image.getImageBytes().length > 0) {
            userDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            userDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return userDto.toBuilder()
                .id(customUser.getId())
                .firstName(customUser.getFirstName())
                .lastName(customUser.getLastName())
                .email(customUser.getEmail())
                .password(customUser.getPassword())
                .language(customUser.getLanguage())
                .country(customUser.getCountry())
                .phoneNumber(customUser.getPhoneNumber())
                .build();
    }

    public CustomUser userDtoToUser(UserDto userDto) {
        return CustomUser.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .avatar(new Image(imageConvertor.convertStringToByteImage(userDto.getAvatar())))
                .language(userDto.getLanguage())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .country(userDto.getCountry())
                .roles(Collections.singletonList(Role.CLIENT))
                .build();
    }
}
