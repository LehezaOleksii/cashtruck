package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {

    private final ClientService clientService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;


    @PostMapping(path = "/login")
    public ModelAndView registerNewClient(@RequestBody CreateClientDto createClientDto) {
        ModelAndView modelAndView;
        if (clientService.findByEmail(createClientDto.getEmail()) != null) {
            modelAndView = new ModelAndView("redirect:/client/login");
            modelAndView.addObject("client", createClientDto);
            //TODO error email is already taken
            return modelAndView;
        }
        clientService.saveClient(createClientDto);
//        modelAndView = new ModelAndView("client/dashboard/{clientId}");
//        modelAndView.addObject("clientId",clientService.findByEmail(clientEmail).getId());
        modelAndView = new ModelAndView("client/dashboard/{clientId}");
        return modelAndView;
    }

    @GetMapping(path = "/{clientId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "clientId") Long clientId) {
        ClientDto client = clientService.getClient(clientId);
        if (client == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        modelAndView.addObject("bank_cards", clientService.getBankCardsByClientId(clientId));
        modelAndView.addObject("client", client);
        modelAndView.addObject("client_statistic", clientService.getClientStatisticByClientId(clientId));
        return modelAndView;
    }

    @PutMapping(path = "/{clientId}/update")
    public ModelAndView updateClientInfo(@PathVariable("clientId") Long clientId, @ModelAttribute("client") CreateClientDto createClientDto) {
        try {
            clientService.updateClientInfo(clientId, createClientDto);
            System.out.println("UPDATE client");
        } catch (Exception e) {
            return new ModelAndView("client/dashboard");
        }
        return new ModelAndView("redirect:/clients/{clientId}");
    }

    @GetMapping({"/{clientId}/bank_cards/save", "/{clientId}/bank_cards/{bankCardId}/save"})
    public ModelAndView clientBankCardsForm(@PathVariable Long clientId, @PathVariable(required = false) Long bankCardId) {
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", clientService.getClient(clientId));//TODO use DTO
        if (Optional.ofNullable(bankCardId).isPresent()) {
            BankCard bankCard = bankCardService.getById(bankCardId);
            modelAndView.addObject("bank_card", CreateBankCardDto.builder().bankName(bankCard.getBankName())
                    .nameOnCard(bankCard.getNameOnCard())
                    .id(bankCardId)
                    .expiringDate(bankCard.getExpiringDate())
                    .cardNumber(bankCard.getCardNumber())
                    .balance(Double.parseDouble(bankCard.getBalance().toString()))
                    .cvv(bankCard.getCvv()).build());
        } else {
            modelAndView.addObject("bank_card", new CreateBankCardDto());
        }
        return modelAndView;
    }

    @PostMapping("/{clientId}/bank_cards")
    public ModelAndView saveBankCardToClient(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        if (!bankCardService.isClientHasCard(clientId, bankCardDto)) {
            savingService.assignBankCardToClient(clientId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        }
        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/bank_cards/update")
    public ModelAndView updateBankCardForm(@PathVariable Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/update_delete_bank_card");
        ClientDto client = clientService.getClient(clientId);
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("bank_cards", client.getSaving().getBankCards());
        return modelAndView;
    }

    @PutMapping("/{clientId}/bank_cards")
    public ModelAndView updateBankCardData(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto) {
        bankCardService.save(bankCardDto);
        savingService.assignBankCardToClient(clientId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/bank_cards/{bankCardId}/remove")
    public ModelAndView removeBankCard(@PathVariable Long clientId, @PathVariable Long bankCardId) {
        bankCardService.removeBankCardForClient(bankCardId, clientId);
        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/categories")
    public ModelAndView viewIncomeAndExpensesDashboard(@PathVariable Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/categories");
        modelAndView.addObject("client", clientService.getClient(clientId));
        modelAndView.addObject("incomes_categories", incomeService.findClientIncomesCategories(clientId));
        modelAndView.addObject("expenses_categories", expenseService.findClientExpensesCategories(clientId) );
        return modelAndView;
    }

    @GetMapping("/{clientId}/categories/{category}")
    public ModelAndView viewIncomeAndExpensesTransactionsByCategoryName(@PathVariable Long clientId,@PathVariable String categoryName) {
        ModelAndView modelAndView = new ModelAndView("client/transactions_details");
        modelAndView.addObject("client", clientService.getClient(clientId));
        modelAndView.addObject("incomes_categories", incomeService.findClientIncomesCategories(clientId));
        return modelAndView;
    }
}