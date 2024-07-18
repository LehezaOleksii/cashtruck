package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String country;
    private String language;
    private Set<BankCard> bankCards;
    private String avatar;
    private String role;
    private String status;
    private String subscription;
    private BigDecimal subscriptionPrice;
    private Date subscriptionFinishDate;
    private List<Transaction> incomes;
    private List<Transaction> expenses;
}
