package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.dto.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SavingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {

    private final ClientService clientService;
    private final BankCardService bankCardService;
    private final SavingService savingService;

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
        Client client = clientService.getClient(clientId);
        if(client==null) {
            return new ModelAndView("login");
        }
        System.out.println("test : " + client.getSaving().getBankCards().size());
        System.out.println("test all: "+ bankCardService.findAll().size());
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
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
            return new ModelAndView("client/dashboard");
        }
        return new ModelAndView("redirect:/clients/{clientId}");
    }

    @GetMapping("/{clientId}/bank_cards/save")
    public ModelAndView clientBankCardsForm(@PathVariable Long clientId){
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", clientService.getClient(clientId));//TODO use DTO
        modelAndView.addObject("bank_card", new BankCardDto());
        return modelAndView;
    }

    @PostMapping("/{clientId}/bank_cards")
    public ModelAndView saveBankCardToClient(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") BankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(clientId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/bank_cards/update")
    public ModelAndView updateBankCardForm(@PathVariable Long clientId){
        ModelAndView modelAndView = new ModelAndView("client/update_delete_bank_card");
        Client client = clientService.getClient(clientId);
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("bank_cards", client.getSaving().getBankCards());
        return modelAndView;
    }

    @PutMapping("/{clientId}/bank_cards")
    public ModelAndView updateBankCardData(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") BankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(clientId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/bank_cards/{bankCardId}/remove")
    public ModelAndView removeBankCard(@PathVariable Long clientId, @PathVariable Long bankCardId){
        bankCardService.removeBankCardForClient(bankCardId,clientId);
        return new ModelAndView("redirect:/clients/" + clientId);
    }
}
