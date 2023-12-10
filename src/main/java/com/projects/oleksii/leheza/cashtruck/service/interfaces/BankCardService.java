package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.dto.BankCardDto;

import java.util.List;

public interface BankCardService {

    void save(BankCard bankCard);

    void save(BankCardDto bankCard);

    BankCard getBankCardByBankNumber(String bankNumber);

    BankCard getById(Long id);

    List<BankCard> findAll();

    boolean isClientHasCard(Long clientId,BankCardDto bankCardDto);

    void removeBankCardForClient(Long bankCardId, Long clientId);
}
