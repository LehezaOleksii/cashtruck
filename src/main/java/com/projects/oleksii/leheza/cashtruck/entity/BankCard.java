package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Entity
@Table
public final class BankCard {

	@Id
	@SequenceGenerator(name = "bankcard_sequence", sequenceName = "bankcard_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bankcard_sequence")
	private final Long id;
	private final String bankName;
	private final String number; 
	private final BigDecimal balance;
	private final LocalDateTime expiringDate;
}
