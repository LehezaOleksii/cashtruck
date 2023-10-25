package com.projects.oleksii.leheza.cashtruck.service;

import java.util.List;
import java.util.Optional;

import com.projects.oleksii.leheza.cashtruck.domain.Client;

public interface ClientService {

	public void saveClient(Client client);
	public List<Client> findAll();

	public Client findByEmail(String email);

	public void deleteById(Long clientId);

	public void updateClient(Long clientId, Client client);

	public Client getClient(Long clientId);
}
