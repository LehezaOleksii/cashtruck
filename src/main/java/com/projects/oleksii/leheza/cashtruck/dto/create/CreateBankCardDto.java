package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateBankCardDto {

    @Min(0)
    Long id;
    @NotNull
    @NotBlank
    @Size(max = 100, message = "Must be exactly 16 characters")
    private String bankName;
    @CreditCardNumber(message = "Invalid credit card number")
    private String cardNumber;
    @NotNull
    @NotBlank
    @Size(max = 50, message = "Must be at most 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Only letters are allowed")
    private String nameOnCard;
    @Size(min = 3, max = 3, message = "Must be exactly 3 digits")
    @Pattern(regexp = "\\d{3}", message = "Must contain only digits")
    private String cvv;
    private double balance;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiringDate;
}
