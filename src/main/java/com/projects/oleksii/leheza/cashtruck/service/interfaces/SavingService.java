package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;

import java.util.Collection;
import java.util.List;

public interface SavingService {

     List<Saving> findAll();

     void saveAll(Collection<Saving>savings);

     void assignBankCardToClient(Long clientId, BankCard bankCard) throws IllegalArgumentException;
     void assignBankCardsToClient(Long clientId, List<BankCard>bankCards);
     void assignBankCardDtoToClient(Long clientId, CreateBankCardDto bankCard);
}
