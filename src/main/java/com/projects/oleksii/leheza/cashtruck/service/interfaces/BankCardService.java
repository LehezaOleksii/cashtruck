package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;

import java.util.List;

public interface BankCardService {

    BankCard save(BankCardDto bankCard);

    BankCardDto getById(Long id);

    boolean isClientHasCard(Long userId, String cardNumber);

    boolean isClientHasCard(Long userId, Long cardId);

    void removeBankCardForClient(Long bankCardId, Long clientId);

    BankCard save(BankCard bankCard);

    List<BankCard> findAll();
}
