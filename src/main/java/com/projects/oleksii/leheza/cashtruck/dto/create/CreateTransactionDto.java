package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateTransactionDto {

    @NotEmpty
    @NotBlank
    private String categoryName;
    @NotEmpty
    @NotBlank
    private String transactionName;
    private String cardNumber;
    @PastOrPresent
    private String time;
    private double sum;
    private boolean isIncome;
    @NotBlank(message = "Currency cannot be blank")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 letters")
    private String currencyShortName;
}
