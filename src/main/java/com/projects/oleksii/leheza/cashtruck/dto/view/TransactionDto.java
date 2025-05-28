package com.projects.oleksii.leheza.cashtruck.dto.view;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private int currencyCode;
    private int delimiter;
    private double sum;
}
