package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findClientByCustomUser_Email(String email); // TODO Optional<Client>

    @Query("SELECT c.customUser.avatar.imageBytes FROM Client c WHERE c.id = :clientId")
    byte[] findAvatarByClientId(@Param("clientId") Long clientId);
}
