package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateTransactionDto {

    @Enumerated(EnumType.ORDINAL)
    private String transactionType;
    @NotEmpty
    @NotBlank
    private String categoryName;
    @CreditCardNumber(message = "Invalid credit card number")
    private String cardNumber;
    @PastOrPresent
    private String time;
    @DecimalMin(value = "0")
    private BigDecimal sum;
}
