package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BankCardDto {
    @Min(0)
    private Long id;
    @NotNull(message = "Bank name cannot be null")
    @NotBlank(message = "Bank name cannot be blank")
    @Size(max = 100, message = "Must be exactly 16 characters")
    private String bankName;
    private String cardNumber;
    @NotNull(message = "Name on card cannot be null")
    @NotBlank(message = "Name on card cannot be blank")
    @Size(max = 50, message = "Must be at most 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Only letters are allowed")
    private String cardHolder;
    @Size(min = 3, max = 3, message = "Must be exactly 3 digits")
    @Pattern(regexp = "\\d{3}", message = "Must contain only digits")
    private String cvv;
    private double balance;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiringDate;
}
