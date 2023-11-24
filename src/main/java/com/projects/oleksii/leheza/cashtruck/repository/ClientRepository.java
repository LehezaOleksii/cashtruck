package com.projects.oleksii.leheza.cashtruck.repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
