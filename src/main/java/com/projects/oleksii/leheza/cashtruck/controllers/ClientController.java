package com.projects.oleksii.leheza.cashtruck.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.oleksii.leheza.cashtruck.entity.Client;
import com.projects.oleksii.leheza.cashtruck.services.ClientService;

@RestController
@RequestMapping(path = "/client")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@GetMapping(path = "/all")
	public List<Client> showAllClients() {
		return clientService.findAll();
	}

	@GetMapping(path = "/save")
	public void RegisterNewClient(@RequestBody Client client) {
		clientService.addNewClient(client);
	}

	@GetMapping(path = "/{clientId}")
	public void showClient(@PathVariable(value = "clientId") Long clientId) {

	}

	@DeleteMapping(path = "{clientId}")
	public void deletClientById(@PathVariable(value = "clientId") Long clientId) {
		clientService.deleteClientById(clientId);
	}

	@PutMapping(path = { "clientId" })
	public void updateStudent(@PathVariable("clientId") Long clientId, @ModelAttribute("client") Client client) {
		clientService.updateClient(clientId, client);
	}
}
