package com.projects.oleksii.leheza.cashtruck.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projects.oleksii.leheza.cashtruck.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query ("SELECT c FROM client c where c.email = ?1")
	Optional<Client> findClientByEmail(String email);
}
