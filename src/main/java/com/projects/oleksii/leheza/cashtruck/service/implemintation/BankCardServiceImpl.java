package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.exception.DomainException;
import com.projects.oleksii.leheza.cashtruck.repository.BankCardRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SavingRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {

    private final BankCardRepository bankCardRepository;
    private final SavingRepository savingRepository;

    @Override
    public void save(BankCard bankCard) {
        bankCardRepository.save(bankCard);
    }


    @Override
    public List<BankCard> getBankCardsByClientId(Long clientId) throws DomainException {
        Set<BankCard> bankCards = savingRepository.findBankCardsByClientId(clientId);
        if (bankCards != null) {
            return bankCards.stream().toList();
        } else {
            throw new DomainException("Bank cards not found for client ");
        }
    }

    @Override
    public List<BankCard> findAll() {
        return bankCardRepository.findAll();
    }
}
