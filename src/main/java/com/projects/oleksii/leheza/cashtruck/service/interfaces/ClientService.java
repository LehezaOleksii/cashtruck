package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;

import java.util.List;

public interface ClientService {

    void saveClient(CreateClientDto createClientDto);

    void saveClient(Client client);

    List<Client> findAll();

    Client findByEmail(String email);

    void deleteById(Long clientId);

    void updateClientInfo(Long clientId, CreateClientDto createClientDto);

    void updateClient(Long clientId, Client client);

    ClientDto getClient(Long clientId);

    //For UI
    ClientStatisticDto getClientStatisticByClientId(Long clientId);
    void addTransaction(Long clientId, Transaction transaction);
    //TODO use only one method (update) use tranisction

    List<BankCard> getBankCardsByClientId(Long clientId);
}
