package com.projects.oleksii.leheza.cashtruck.service;

import java.util.List;
import java.util.Optional;

import com.projects.oleksii.leheza.cashtruck.domain.Client;

public interface ClientService {

	public List<Client> findAll();

	void addNewClient(Client client);

	public Optional<Client> findByEmail(String email);

	public void deleteClientById(Long clientId);

	public void updateClient(Long clientId, Client client);
}
