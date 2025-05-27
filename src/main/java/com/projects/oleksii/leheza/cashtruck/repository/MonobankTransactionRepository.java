package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonobankTransactionRepository extends JpaRepository<MonobankTransaction, Long> {

    @Query("""
                SELECT mt.monobankId
                FROM MonobankTransaction mt
                JOIN mt.transaction t
                JOIN t.bankCard bc
                JOIN MonobankAccount ma ON ma.maskedPan = bc.cardNumber
                WHERE ma.monobankId = :monobankId
            """)
    List<String> findTransactionIdsByMonobankId(@Param("monobankId") String monobankId);
}
