package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.domain.Income;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientStatisticDto;

import java.util.List;

public interface ClientService {

	  void saveClient(ClientDto clientDto);

	  void saveClient(Client client);
	  List<Client> findAll();

	  Client findByEmail(String email);

	  void deleteById(Long clientId);

	  void updateClientInfo(Long clientId, ClientDto clientDto);

	  void updateClient(Long clientId, Client client);

	 Client getClient(Long clientId);

	//For UI
	  ClientStatisticDto getClientStatisticByClientId(Long clientId) ;

	//TODO use only one method (update) use tranisction

	void addIncome(Long clientId, Income income);
	//TODO use only one method (update) use tranisction

	void addExpense(Long clientId, Expense expense);

	List<BankCard> getBankCardsByClientId(Long clientId);
}
