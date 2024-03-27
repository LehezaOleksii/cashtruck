package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTransactionsByCategoryTransactionType(TransactionType transactionType);

    @Query("SELECT t FROM CustomUser u JOIN u.transactions t JOIN t.category cat WHERE u.id = ?1 AND cat.transactionType = ?2")
    List<Transaction> findTransactionsByClientIdAndTransactionType(Long userId, TransactionType transactionType);


    @Query("SELECT t FROM CustomUser u JOIN u.transactions t JOIN t.category cat WHERE u.id = ?1 AND cat.name = ?2")
    List<Transaction> findTransactionsByClientIdAndCategoryName(Long userId, String categoryName);
}
