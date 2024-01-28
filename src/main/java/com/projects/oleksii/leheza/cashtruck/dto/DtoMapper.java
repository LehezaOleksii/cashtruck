package com.projects.oleksii.leheza.cashtruck.dto;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DtoMapper {

    public TransactionDto transactionToDto(Transaction transaction) {
        return TransactionDto.builder()
                .sum(transaction.getBankTransaction().getSum())
                .name(transaction.getBankTransaction().getName())
                .time(transaction.getBankTransaction().getTime())
                .category(transaction.getCategory().getName())
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
                .categoryImage(category.getCategoryImage())
                .transactionType(category.getTransactionType())
                .name(category.getName())
                .build();
    }
}
