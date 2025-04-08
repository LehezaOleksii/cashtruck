package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonobankIntegrationRepository extends JpaRepository<MonobankIntegration, Long> {

    @Query("SELECT mi FROM User u JOIN u.monobankIntegration mi WHERE u.id=:userId")
    Optional<MonobankIntegration> findByUserId(Long userId);
}
