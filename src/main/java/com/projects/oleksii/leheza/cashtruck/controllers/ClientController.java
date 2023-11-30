package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.dto.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {

    private final ClientService clientService;
    private final BankCardService bankCardService;
    private final SavingService savingService;

    @GetMapping(path = "/all")
    public ModelAndView showAllClients() {
        ModelAndView modelAndView = new ModelAndView("client/all");
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

    @GetMapping(path = "/{clientId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "clientId") Long clientId) {
        ModelAndView modelAndView;
        Client client = clientService.getClient(clientId);
        if(client==null) {
            return new ModelAndView("login");
        }
        modelAndView = new ModelAndView("client/dashboard");
//      modelAndView.addObject("clientId", client.getId());
        modelAndView.addObject("bank_cards",clientService.getBankCardsByClientId(clientId));
        modelAndView.addObject("client", client);
        modelAndView.addObject("client_statistic", clientService.getClientStatisticByClientId(clientId));
        return modelAndView;
    }



    @PutMapping(path = "/{clientId}/update")
    public ModelAndView updateClientInfo(@PathVariable("clientId") Long clientId, @ModelAttribute("client") ClientDto clientDto) {
        try {
            clientService.updateClientInfo(clientId, clientDto);
            System.out.println("UPDATE client");
        } catch (Exception e) {
            return new ModelAndView("client/dashboard/{clientId}");
        }
        return new ModelAndView("redirect:/client/dashboard/{clientId}");
    }

    @DeleteMapping(path = "/{clientId}")
    public ModelAndView deleteClientById(@PathVariable(value = "clientId") Long clientId) {
        clientService.deleteById(clientId);
        return new ModelAndView("client/all");
    }
    @GetMapping("/{bankCardId}")
    public ModelAndView getClientBankCards(@PathVariable("bankCardId") Long bankCardId, @ModelAttribute("bank card")BankCardDto){
        List<BankCard> bankCards = bankCardService.getBankCardsByClientId(clientId);
        ModelAndView modelAndView = new ModelAndView("dasdas");
        modelAndView.addObject("bank cards",bankCards);
        return modelAndView;
    }

    @PutMapping("")
    public ModelAndView saveBankCardToClient(@ModelAttribute("bankCard") BankCardDto bankCard){
        savingService.assignBankCardDtoToClient(clientId,bankCard);
//         redirect ?
        return modelAndView;
    }

    @DeleteMapping("/{bankCardId}")
    public ModelAndView deleteBankCardForClient(){
        bankCardService.g
//         redirect ?
        return modelAndView;
    }
}
