package com.projects.oleksii.leheza.cashtruck.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.oleksii.leheza.cashtruck.entity.Client;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;

import jakarta.transaction.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Override
	public void addNewClient(Client client) {
		Optional<Client> clientOptional = clientRepository.findClientByEmail(client.getEmail());
		if (clientOptional.isPresent()) {
			throw new IllegalStateException("Email taken");
		}
		clientRepository.save(client);
	}

	@Override
	public Optional<Client> findByEmail(String email) {
		return clientRepository.findClientByEmail(email);
	}

	public List<Client> findAll() {
		return clientRepository.findAll();
	}

	@Override
	public void deleteClientById(Long clientId) {
		boolean exists = clientRepository.existsById(clientId);
		if (!exists) {
			throw new IllegalStateException("Client with" + clientId + "does not exists");
		}
		clientRepository.deleteById(clientId);
	}

	@Override
	@Transactional
	public void updateClient(Long clientId, Client client) {
		String updatedEmail = client.getEmail();
		Optional<Client> currentClientOptional = clientRepository.findById(clientId);
		if (existByEmail(client) && currentClientOptional.get().getEmail() != updatedEmail) {
			throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
		}
		Client currentClient = currentClientOptional.get().toBuilder().firstname(client.getFirstname())
				.lastname(client.getLastname()).income(client.getIncome()).expenses(client.getExpenses())
				.email(updatedEmail).build();
		clientRepository.save(currentClient);
	}

	private boolean existByEmail(Client client) {
		Optional<Client> clientOptional = clientRepository.findClientByEmail(client.getEmail());
		if (clientOptional.isPresent()) {
			return true;
		} else {
			return false;
		}
	}
}
