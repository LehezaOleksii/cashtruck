package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Long> {

    @Query("SELECT s.bankCards FROM Saving s WHERE s.id = :clientId")
    Set<BankCard> findBankCardsByClientId(Long clientId);
}
