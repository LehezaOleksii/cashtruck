package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.service.ClientService;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(path = "/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping(path = "/all")
    public ModelAndView showAllClients() {
        ModelAndView modelAndView = new ModelAndView("client/all");//TODO first 20 than next 20 (front)
        modelAndView.addObject("clients", clientService.findAll());
        return modelAndView;
    }

    @PostMapping(path = "/login")
    public ModelAndView registerNewClient(@RequestBody ClientDto clientDto) {
        ModelAndView modelAndView;
        if (clientService.findByEmail(clientDto.getEmail()) != null) {
            modelAndView = new ModelAndView("redirect:/client/login");
            modelAndView.addObject("client", clientDto);
            //TODO error email is already taken
            return modelAndView;
        }
        clientService.saveClient(clientDto);
//        modelAndView = new ModelAndView("client/dashboard/{clientId}");
//        modelAndView.addObject("clientId",clientService.findByEmail(clientEmail).getId());
        modelAndView = new ModelAndView("client/dashboard/{clientId}");
        return modelAndView;
    }

    @GetMapping(path = "/inforamtion/{clientId}")
    public ModelAndView showClientInfo(@PathVariable(value = "clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/information");
        Client client = clientService.getClient(clientId);
//        modelAndView.addObject("clientId", client.getId());
        ClientDto clientDto = ClientDto.builder()
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .email(client.getEmail())
                .password(client.getPassword())
                .build();
        modelAndView.addObject("client", clientDto);
        return modelAndView;
    }

    @GetMapping(path = "/dashboard/{clientId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        Client client = clientService.getClient(clientId);
//        modelAndView.addObject("clientId", client.getId());
        modelAndView.addObject("client", client);
        return modelAndView;
    }

    @PutMapping(path = {"/update/{clientId}"})
    public ModelAndView updateClientInfo(@PathVariable("clientId") Long clientId, @ModelAttribute("client") ClientDto clientDto) {
        try {
            clientService.updateClientInfo(clientId, clientDto);
            System.out.println("UPDATE client");
        } catch (Exception e) {
            return new ModelAndView("client/dashboard/{clientId}");
        }
        return new ModelAndView("redirect:/client/dashboard/{clientId}");
    }

    @DeleteMapping(path = "/delete/{clientId}")
    public ModelAndView deleteClientById(@PathVariable(value = "clientId") Long clientId) {
        clientService.deleteById(clientId);
        return new ModelAndView("client/all");
    }
}
