package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;

import java.util.Collection;
import java.util.List;

public interface SavingService {

     List<Saving> findAll();

     Collection<Saving> saveAll(Collection<Saving>savings);

     void assignBankCardToClient(Long clientId, BankCard bankCard) throws IllegalArgumentException;
}
