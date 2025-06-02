package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.dto.view.DashboardCategoryDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
        PagingAndSortingRepository<Category, Long> {

    Optional<Category> findByName(String Name);

    List<Category> findByTransactionType(TransactionType transactionType);

    @Query("SELECT c FROM Transaction t " +
            "JOIN t.category c " +
            "JOIN t.bankCard bc " +
            "JOIN bc.user u " +
            "WHERE c.transactionType IN :transactionTypes AND u.id = :clientId " +
            "AND ((:isPositive = true AND t.bankTransaction.sum > 0) OR (:isPositive = false AND t.bankTransaction.sum < 0))")
    List<Category> findCategoriesByTransactionTypesAndClientId(
            @Param("transactionTypes") List<TransactionType> transactionTypes,
            @Param("clientId") Long clientId,
            @Param("isPositive") boolean isPositiveTransactionSum);

    @Query("""
            SELECT new com.projects.oleksii.leheza.cashtruck.dto.view.DashboardCategoryDto(
                t.category.name,
                SUM(ABS(bt.sum)),
                c.shortName
                )
            FROM Transaction t
            JOIN t.bankTransaction bt
            JOIN t.bankCard bc
            JOIN bc.currency c
            WHERE bc.user.id = :userId
              AND bt.time >= :startDate
            GROUP BY t.category.name, c.shortName
            """)
    List<DashboardCategoryDto> getCategorySums(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate);

    @Query("""
            SELECT new com.projects.oleksii.leheza.cashtruck.dto.view.DashboardCategoryDto(
                t.category.name,
                SUM(ABS(bt.sum)),
                c.shortName
                )
            FROM Transaction t
            JOIN t.bankTransaction bt
            JOIN t.bankCard bc
            JOIN bc.currency c
            WHERE bc.user.id = :userId
            AND bt.time >= :startDate
            AND bc.cardNumber = :cardNumber
            GROUP BY t.category.name, c.shortName
            """)
    List<DashboardCategoryDto> getCategorySums(@Param("userId") Long userId,
                                               @Param("cardNumber") String cardNumber,
                                               @Param("startDate") LocalDateTime startDater);

    Page<Category> findAll(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.mccs LIKE %:mcc%")
    Optional<Category> findByMcc(@Param("mcc") String mcc);
}
