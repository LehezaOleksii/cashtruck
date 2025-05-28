package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DashboardIncomeExpensesDiagramDto {

    private Map<String, Double> incomes;
    private Map<String, Double> expenses;
}
