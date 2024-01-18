package com.projects.oleksii.leheza.cashtruck.dto.create;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;
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
    @Column(length = 100)
    private String bankName;
    @Column(length = 16)
    @Pattern(regexp = "^[0-9]+$", message = "Only numbers are allowed")
    private String cardNumber;
    @Column(length = 50)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Only letters are allowed")
    private String nameOnCard;
    @Column(length = 3)
    @Pattern(regexp = "^[0-9]+$", message = "Only numbers are allowed")
    private String cvv;
    private double balance;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiringDate;
}
