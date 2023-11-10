package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpensesCategoryRepository extends JpaRepository<ExpensesCategory,Long> {

    @Query("SELECT exp_c FROM ExpensesCategory exp_c where exp_c.categoryName = ?1")
    Optional<ExpensesCategory> findByCategoryName(String categoryName);
}
