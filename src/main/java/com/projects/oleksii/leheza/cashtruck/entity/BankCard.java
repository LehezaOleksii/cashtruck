package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public final class BankCard {

	private final Long id;
	private final String bankName;
	private final String number;
	private final Saving saving;
	private final BigDecimal balance;
	private final LocalDateTime expiringDate;
}
