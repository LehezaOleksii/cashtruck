package com.projects.oleksii.leheza.cashtruck.entity;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
public final class Saving {

	@Id
	@SequenceGenerator(name = "saving_sequence", sequenceName = "saving_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saving_sequence")
	private final Long id;
	private final BigDecimal cash;
	@OneToMany
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_bank_card"))
	private Set<BankCard> bankCards;
}
