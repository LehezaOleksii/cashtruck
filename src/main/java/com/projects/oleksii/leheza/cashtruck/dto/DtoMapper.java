package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ClientUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
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

    public CreateClientDto clientToCreateDto(Client client) {
        CustomUser user = client.getCustomUser();
        return CreateClientDto.builder()
                .email(user.getEmail())
                .password(client.getCustomUser().getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
//                .avatar(user.getAvatar().getBytes())
                .build();
    }

    public ClientDto clientToDto(Client client) {
        CustomUser user = client.getCustomUser();
        return ClientDto.builder()
                .id(client.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .language(user.getLanguage())
                .country(user.getCountry())
                .phoneNumber(user.getPhoneNumber())
                .saving(client.getSaving())
                .expenses(client.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .incomes(client.getTransactions().stream()
                        .filter(transaction -> transaction.getCategory().getTransactionType()
                                .equals(TransactionType.EXPENSE))
                        .collect(Collectors.toList()))
                .avatar(user.getAvatar().getImageBytes())
                .build();
    }

    public Client dtoToClient(ClientDto clientdto) {
        List<Transaction> transactions = clientdto.getIncomes();
        transactions.addAll(clientdto.getExpenses());
        CustomUser user = CustomUser.builder()
                .id(clientdto.getId())
                .firstName(clientdto.getFirstName())
                .lastName(clientdto.getLastName())
                .avatar(new Image(clientdto.getAvatar()))
                .language(clientdto.getLanguage())
                .email(clientdto.getEmail())
                .password(clientdto.getPassword())
                .phoneNumber(clientdto.getPhoneNumber())
                .country(clientdto.getCountry())
                .role(UserRole.Client)
                .build();
        return Client.builder()
                .customUser(user)
                .saving(clientdto.getSaving())
                .transactions(transactions)
                .build();
    }

    public ClientUpdateDto clientToClientUpdateDto(Client client) {
        CustomUser user = client.getCustomUser();
        ClientUpdateDto clientUpdateDto = new ClientUpdateDto();
//        if (user.getAvatar() != null) {
//            clientUpdateDto.setAvatar(imageConvertor.convertByteImageToString(user.getAvatar().getImageBytes()));
//        }
        Image image = client.getCustomUser().getAvatar();
        Long clientId = client.getId();
        if (image != null && image.getImageBytes().length > 0) {
            clientUpdateDto.setAvatar((imageConvertor.convertByteImageToString(image.getImageBytes())));
        } else {
            clientUpdateDto.setAvatar(imageService.getDefaultAvatarImage());
        }
        return clientUpdateDto.toBuilder()
                .id(client.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .language(user.getLanguage())
                .country(user.getCountry())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public Client updateClientDtoToClient(ClientUpdateDto clientUpdateDto) {
        CustomUser user = CustomUser.builder()
                .id(clientUpdateDto.getId())
                .firstName(clientUpdateDto.getFirstName())
                .lastName(clientUpdateDto.getLastName())
                .avatar(new Image(imageConvertor.convertStringToByteImage(clientUpdateDto.getAvatar())))
                .language(clientUpdateDto.getLanguage())
                .email(clientUpdateDto.getEmail())
                .password(clientUpdateDto.getPassword())
                .phoneNumber(clientUpdateDto.getPhoneNumber())
                .country(clientUpdateDto.getCountry())
                .role(UserRole.Client)
                .build();
        return Client.builder()
                .customUser(user)
                .build();
    }
}
