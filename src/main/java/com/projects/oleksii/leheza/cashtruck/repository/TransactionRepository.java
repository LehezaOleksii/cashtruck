package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTransactionsByCategoryTransactionType(TransactionType transactionType);

    @Query("SELECT t FROM Client c JOIN c.transactions t JOIN t.category cat WHERE c.id = ?1 AND cat.transactionType = ?2")
    List<Transaction> findTransactionsByClientIdAndTransactionType( Long clientId, TransactionType transactionType);


    @Query("SELECT t FROM Client c JOIN c.transactions t JOIN t.category cat WHERE c.id = ?1 AND cat.name = ?2")
    List<Transaction> findTransactionsByClientIdAndCategoryName( Long clientId, String categoryName);
}
