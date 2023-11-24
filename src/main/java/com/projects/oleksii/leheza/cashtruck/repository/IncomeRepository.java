package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income,Long> {

    @Query("SELECT i FROM Income i WHERE i.transaction.time >= :startDate AND i.transaction.time <= :endDate AND i.transaction.from.id = :clientId")
    List<Income> findIncomesForPeriod(@Param("clientId") Long clientId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate
    );
}
