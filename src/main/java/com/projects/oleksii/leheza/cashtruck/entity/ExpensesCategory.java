package com.projects.oleksii.leheza.cashtruck.entity;

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
public final class ExpensesCategory {

	private final Long id;
	private final String categoryName;
}
