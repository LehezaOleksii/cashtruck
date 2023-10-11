package com.projects.oleksii.leheza.cashtruck.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.oleksii.leheza.cashtruck.entity.Client;

public interface ClientRepository extends  JpaRepository<Client,Long> {

}
