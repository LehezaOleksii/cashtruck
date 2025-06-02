package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.view.DashboardTransactionDto;
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

    @Query("SELECT t FROM Transaction t JOIN t.bankCard bc JOIN bc.user u WHERE u.id = ?1")
    List<Transaction> findTransactionsByClientId(Long userId);

    Page<Transaction> findAll(Specification<?> specification, Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.bankCard IN :bankCards
            AND t.bankTransaction.time >= :startDate
            AND t.bankTransaction.time <= :endDate
            AND ((:isPositiveTransactionSum = true AND t.bankTransaction.sum > 0)
                 OR (:isPositiveTransactionSum = false AND t.bankTransaction.sum < 0))
            """)
    List<Transaction> findByBankCardsAndDateRangeAndTransactionTypes(@Param("bankCards") List<BankCard> bankCards,
                                                                     @Param("startDate") LocalDateTime startDate,
                                                                     @Param("endDate") LocalDateTime endDate,
                                                                     @Param("isPositiveTransactionSum") boolean isPositiveTransactionSum);


    @Query("""
            SELECT t FROM Transaction t
            WHERE t.bankCard IN :bankCards
            AND t.bankTransaction.time >= :startDate
            AND t.bankTransaction.time <= :endDate
            AND t.bankCard.cardNumber = :cardNumber
            AND ((:isPositiveTransactionSum = true AND t.bankTransaction.sum > 0)
                 OR (:isPositiveTransactionSum = false AND t.bankTransaction.sum < 0))
            """)
    List<Transaction> findByBankCardsAndDateRangeAndTransactionTypes(@Param("bankCards") List<BankCard> bankCards,
                                                                     @Param("startDate") LocalDateTime startDate,
                                                                     @Param("endDate") LocalDateTime endDate,
                                                                     @Param("isPositiveTransactionSum") boolean isPositiveTransactionSum,
                                                                     @Param("cardNumber") String cardNumber);

    @Query("""
            SELECT new com.projects.oleksii.leheza.cashtruck.dto.view.DashboardTransactionDto(
                t.category.name,
                bt.name,
                bt.sum,
                bc.currency.shortName,
                bt.currency.delimiter
            )
            FROM Transaction t
            JOIN t.bankTransaction bt
            JOIN t.bankCard bc
            WHERE bc.user.id = :userId
            ORDER BY bt.time DESC
            """)
    List<DashboardTransactionDto> findLastTransactionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT new com.projects.oleksii.leheza.cashtruck.dto.view.DashboardTransactionDto(
                t.category.name,
                bt.name,
                bt.sum,
                bc.currency.shortName,
                bt.currency.delimiter
            )
            FROM Transaction t
            JOIN t.bankTransaction bt
            JOIN t.bankCard bc
            WHERE bc.user.id = :userId AND 
                  bc.cardNumber = :cardNumber
            ORDER BY bt.time DESC
            """)
    List<DashboardTransactionDto> findLastTransactionsByUserId(@Param("userId") Long userId, String cardNumber, Pageable pageable);

    @Query("SELECT t FROM Transaction t JOIN t.bankCard WHERE t.bankCard.cardNumber = :cardNumber")
    List<Transaction> findByBankCardNumber(String cardNumber);
}
