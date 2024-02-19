package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ClientUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientHeaderDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.*;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {

    private final ClientService clientService;
    private final BankCardService bankCardService;
    private final SavingService savingService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final ImageConvertor imageConvertor;


    @PostMapping(path = "/login")
    public ModelAndView registerNewClient(@RequestBody CreateClientDto createClientDto) {
        ModelAndView modelAndView;
        if (clientService.findByEmail(createClientDto.getEmail()) != null) {
            Client client = clientService.saveClient(createClientDto);
            modelAndView = new ModelAndView("redirect:/client/login");
            modelAndView.addObject("client", clientService.getHeaderClientData(client.getId()));
            //TODO error email is already taken
            return modelAndView;
        } else {
            return null; //TODO
        }
    }

    @GetMapping(path = "/{clientId}")
    public ModelAndView showClientDashboard(@PathVariable(value = "clientId") Long clientId) {
        ClientHeaderDto client = clientService.getHeaderClientData(clientId);
        if (client == null) {
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("client/dashboard");
        modelAndView.addObject("bank_cards", clientService.getBankCardsByClientId(clientId));
        modelAndView.addObject("client", client);
        modelAndView.addObject("client_statistic", clientService.getClientStatisticByClientId(clientId));
        return modelAndView;
    }

    @GetMapping({"/{clientId}/bank_cards/save", "/{clientId}/bank_cards/{bankCardId}/save"})
    public ModelAndView clientBankCardsForm(@PathVariable Long clientId, @PathVariable(required = false) Long bankCardId) {
        ModelAndView modelAndView = new ModelAndView("client/add_bank_card");
        modelAndView.addObject("client", clientService.getHeaderClientData(clientId));//TODO use DTO
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
    public ModelAndView saveBankCardToClient(@PathVariable Long clientId, @Valid @ModelAttribute("bank_card") CreateBankCardDto bankCardDto, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("client/add_bank_card")
                    .addObject("client", clientService.getHeaderClientData(clientId));
        }
        bankCardService.save(bankCardDto);
        if (!bankCardService.isClientHasCard(clientId, bankCardDto)) {
            savingService.assignBankCardToClient(clientId, bankCardService.getBankCardByBankNumber(bankCardDto.getCardNumber()));
        }

        return new ModelAndView("redirect:/clients/" + clientId);
    }

    @GetMapping("/{clientId}/bank_cards/update")
    public ModelAndView updateBankCardForm(@PathVariable Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/update_delete_bank_card");
        ClientHeaderDto clientHeaderDto = clientService.getHeaderClientData(clientId);
        modelAndView.addObject("client", clientHeaderDto);
        modelAndView.addObject("bank_cards", clientService.getClient(clientId).getSaving().getBankCards());
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
        modelAndView.addObject("client", clientService.getHeaderClientData(clientId));//TODO Optimise maybe (it will be a lot of dto)
        modelAndView.addObject("incomes_categories", transactionService.findClientIncomeCategoriesByClientId(clientId));
        modelAndView.addObject("expenses_categories", transactionService.findClientExpenseCategoriesByClientId(clientId));
        return modelAndView;
    }

    @GetMapping("/{clientId}/categories/{categoryName}")
    public ModelAndView viewTransactionsByCategoryName(@PathVariable Long clientId, @PathVariable String categoryName) {
        ModelAndView modelAndView = new ModelAndView("client/transactions_details");
        modelAndView.addObject("client", clientService.getHeaderClientData(clientId));
        modelAndView.addObject("category", categoryService.findByName(categoryName));
        modelAndView.addObject("transactions", transactionService.findTransactionsByClientIdAndCategoryName(clientId, categoryName));
        return modelAndView;
    }

    @GetMapping("/{clientId}/profile")
    public ModelAndView updateClientAccountForm(@PathVariable Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/profile");
        modelAndView.addObject("client", clientService.getHeaderClientData(clientId));
        modelAndView.addObject("clientId", clientId);
        ClientUpdateDto clientUpdateDto = clientService.getClientUpdateDto(clientId);
        modelAndView.addObject("clientDto", clientUpdateDto);
        return modelAndView;
    }

    @PostMapping("/{clientId}/update")
    public ModelAndView updateClientAccount(@PathVariable Long clientId, @Valid @ModelAttribute("clientDto") ClientUpdateDto clientUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("client/profile")
                    .addObject("client", clientService.getHeaderClientData(clientId))
                    .addObject("clientId", clientId);
        } else {
            if (!avatar.isEmpty()) {
                try {
                    clientUpdateDto.setAvatar(imageConvertor.convertByteImageToString(avatar.getBytes()));
                } catch (IOException e) {
                    return new ModelAndView("redirect:/clients/" + clientId + "/profile")
                            .addObject("errorMessage", e.getMessage());
                }
            }
            try {
                clientService.updateClientInfo(clientId, clientUpdateDto);
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:/clients/" + clientId + "/profile");
                modelAndView.addObject("client", clientService.getHeaderClientData(clientId));
                return modelAndView.addObject("errorMessage", e.getMessage());
            }
            return new ModelAndView("redirect:/clients/" + clientId);
        }
    }
}