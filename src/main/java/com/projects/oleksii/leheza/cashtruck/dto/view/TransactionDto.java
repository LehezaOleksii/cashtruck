package com.projects.oleksii.leheza.cashtruck.dto.view;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class TransactionDto {
    private String name;
    private String category;
    private String transactionType;
    private LocalDateTime time;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal sum;
}
