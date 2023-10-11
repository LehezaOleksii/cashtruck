package com.projects.oleksii.leheza.cashtruck.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public final class Client extends User {

	private final Income income;
	private final Expenses expenses;
	private final Saving saving;
}
