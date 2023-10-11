package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
public final class Income {

	private final Long id;
	private final Client client;
	private final IncomeCategory incomeCategory;
	private final ExpensesCategory category;
	private final BigDecimal sum;
}
