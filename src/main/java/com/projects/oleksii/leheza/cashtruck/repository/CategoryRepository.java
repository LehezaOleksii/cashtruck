package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
        PagingAndSortingRepository<Category, Long> {

    Optional<Category> findByName(String Name);

    List<Category> findByTransactionType(TransactionType transactionType);

    @Query("SELECT cat FROM User u JOIN u.bankCards bc JOIN bc.transactions t JOIN t.category cat WHERE cat.transactionType = ?1 AND u.id = ?2")
    List<Category> findCategoriesByTransactionTypeAndClientId(TransactionType transactionType, Long clientId);

    Page<Category> findAll(Pageable pageable);
}
