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

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.service.ClientService;

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
    public void registerNewClient(@RequestBody Client client) {
        clientService.saveClient(client);
    }

    @GetMapping(path = "/{clientId}")
    public void showClient(@PathVariable(value = "clientId") Long clientId) {
        clientService.getClient(clientId);
    }

    @DeleteMapping(path = "delete/{clientId}")
    public void deletClientById(@PathVariable(value = "clientId") Long clientId) {
        clientService.deleteById(clientId);
    }

    @PutMapping(path = {"update/{clientId}"})
    public void updateStudent(@PathVariable("clientId") Long clientId, @ModelAttribute("client") Client client) {
        clientService.updateClient(clientId, client);
    }
}
