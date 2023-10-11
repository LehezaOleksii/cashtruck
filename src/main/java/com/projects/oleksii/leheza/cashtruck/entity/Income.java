package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
public final class Income {

	@Id
	@SequenceGenerator(name = "income_sequence", sequenceName = "income_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_sequence")
	private final Long id;
	@OneToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_income_category"))
	private final IncomeCategory incomeCategory;
	private final LocalDateTime time;
	@Size(min = 0)
	private final BigDecimal sum;
}
