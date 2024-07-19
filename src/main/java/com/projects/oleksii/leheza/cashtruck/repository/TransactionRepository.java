package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends
        JpaRepository<Transaction, Long>,
        PagingAndSortingRepository<Transaction, Long> {

    List<Transaction> findTransactionsByCategoryTransactionType(TransactionType transactionType);

    @Query("SELECT t FROM User u JOIN u.bankCards bc JOIN bc.transactions t WHERE u.id = ?1")
    List<Transaction> findTransactionsByClientId(Long userId);

    Page<Transaction> findAll(Specification<?> specification, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.bankCard IN :bankCards AND t.bankTransaction.time >= :startDate AND t.bankTransaction.time <= :endDate AND t.category.transactionType = :transactionType")
    List<Transaction> findTransactionForPeriod(@Param("bankCards") List<BankCard> bankCards,
                                               @Param("transactionType") TransactionType transactionType,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}
