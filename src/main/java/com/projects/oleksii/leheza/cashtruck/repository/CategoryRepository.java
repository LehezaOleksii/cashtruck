package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String Name);

    List<Category> findByTransactionType(TransactionType transactionType);

    @Query("SELECT cat FROM CustomUser u JOIN u.transactions t JOIN t.category cat WHERE cat.transactionType = ?1 AND u.id = ?2")
    List<Category> findCategoriesByClientId(TransactionType transactionType, Long clientId);

    @Query("SELECT cat FROM CustomUser u JOIN u.transactions t JOIN t.category cat WHERE u.id=?1 AND cat.name = ?2")
    List<Category> findCategoriesByClientIdAndCategoryName(Long clientId, String categoryName);
}
