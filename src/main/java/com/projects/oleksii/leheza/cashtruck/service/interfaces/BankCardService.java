package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;

import java.util.List;

public interface BankCardService {

    public void save(BankCard bankCard);

    public List<BankCard> getBankCardsByClientId(Long clientId);

    public List<BankCard> findAll();
}
