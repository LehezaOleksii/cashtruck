package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DashboardBankCardDto {

    private long id;
    private String bank;
    private String bankName;
    private String cardNumber;
    private String holderName;
    private String currency;
    private int currencyCode;
    private double balance;
    private String currencyShortName;
    private String type;
    private int delimiter;
}
