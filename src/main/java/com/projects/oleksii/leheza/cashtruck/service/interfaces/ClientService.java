package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ClientUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientHeaderDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;

import java.util.List;

public interface ClientService {

    Client saveClient(CreateClientDto createClientDto);

    Client saveClient(Client client);

    List<Client> findAll();

    Client findByEmail(String email);

    void deleteById(Long clientId);

    void updateClientInfo(Long clientId, ClientUpdateDto clientUpdateDto);

    void updateClient(Long clientId, Client client);

    ClientDto getDtoClient(Long clientId);

    CreateClientDto getCreateClientDto(Long clientId);

    //For UI
    ClientStatisticDto getClientStatisticByClientId(Long clientId);

    void addTransaction(Long clientId, Transaction transaction);
    //TODO use only one method (update) use tranisction

    List<BankCard> getBankCardsByClientId(Long clientId);

    ClientDto getClientDto(Long clientId);

    ClientUpdateDto getClientUpdateDto(Long clientId);

    Client getClient(Long clientId);

    ClientHeaderDto getHeaderClientData(Long clientId);
}
