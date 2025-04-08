package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonobankAccountRepository extends JpaRepository<MonobankAccount, Long> {

    @Query("SELECT ma FROM User u JOIN u.monobankIntegration.monobankAccounts ma WHERE u.id = :userId")
    List<MonobankAccount> findByUserId(Long userId);

    @Query("SELECT ma FROM MonobankAccount ma WHERE ma.maskedPan IN :pans")
    List<MonobankAccount> findByPans(List<String> pans);
}
