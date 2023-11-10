package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense,Long> {
}
