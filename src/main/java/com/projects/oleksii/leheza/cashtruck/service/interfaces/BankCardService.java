package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;

import java.util.List;

public interface BankCardService {

    BankCard save(BankCard bankCard);

    BankCard save(CreateBankCardDto bankCard);

    BankCard getBankCardByBankNumber(String bankNumber);

    BankCard getById(Long id);

    List<BankCard> findAll();

    boolean isClientHasCard(Long userId, BankCard bankCard);

    boolean isClientHasCard(Long userId, CreateBankCardDto createBankCardDto);

    boolean isClientHasCard(Long userId, Long cardId);

    void removeBankCardForClient(Long bankCardId, Long clientId);
}
