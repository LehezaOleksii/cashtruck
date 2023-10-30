package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;

import java.util.List;

public interface ClientService {

	public void saveClient(ClientDto clientDto);
	public List<Client> findAll();

	public Client findByEmail(String email);

	public void deleteById(Long clientId);

	public void updateClientInfo(Long clientId, ClientDto clientDto);

	public void updateClient(Long clientId, Client client);

	public Client getClient(Long clientId);
}
