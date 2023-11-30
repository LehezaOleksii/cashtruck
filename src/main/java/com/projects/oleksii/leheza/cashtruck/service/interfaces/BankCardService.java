package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;

import java.util.List;

public interface BankCardService {

    void save(BankCard bankCard);

    List<BankCard> getBankCardsByClientId(Long clientId);

    List<BankCard> findAll();
}
