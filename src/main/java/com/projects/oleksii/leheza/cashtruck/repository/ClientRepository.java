package com.projects.oleksii.leheza.cashtruck.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.oleksii.leheza.cashtruck.domain.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c where c.email = ?1")
    Client findByEmail(String email); // Optional<Client>

    @Query("SELECT e FROM Expense e WHERE e.transaction.time >= :startDate AND e.transaction.time <= :endDate AND e.transaction.from.id = :clientId")
    List<Expense> findExpensesForLastYear(@Param("clientId") Long clientId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate
                                                   );
    @Query("SELECT i FROM Income i WHERE i.transaction.time >= :startDate AND i.transaction.time <= :endDate AND i.transaction.from.id = :clientId")
    List<Income> findIncomesForLastYear(@Param("clientId") Long clientId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate
    );
}
