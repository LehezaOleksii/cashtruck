package com.projects.oleksii.leheza.cashtruck.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "expenses_category")
public final class ExpensesCategory {

	@Id
	@SequenceGenerator(name = "expenses_category_sequence", sequenceName = "expenses_category_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenses_category_sequence")
	private final Long id;
	@Column(name = "category_name")
	@NotEmpty
	@NotBlank
	private final String categoryName;
}
