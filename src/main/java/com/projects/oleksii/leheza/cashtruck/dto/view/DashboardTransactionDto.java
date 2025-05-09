package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DashboardTransactionDto {

    private String categoryName;
    private String description;
    private long sum;
    private String currencyShortName;
    private long delimiter;
}
