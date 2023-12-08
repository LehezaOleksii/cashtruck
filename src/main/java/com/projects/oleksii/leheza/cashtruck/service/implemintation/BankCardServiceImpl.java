package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.dto.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.repository.BankCardRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SavingRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {

    private final BankCardRepository bankCardRepository;
    private final ClientRepository clientRepository;


    @Override
    public void save(BankCard bankCard) {
        bankCardRepository.save(bankCard);
    }

    @Override
    public void save(BankCardDto bankCardDto) {
        if (bankCardRepository.findCardByNumber(bankCardDto.getCardNumber()).isPresent()) {
            return;
        }
        BankCard bankCard = BankCard.builder()
                .cvv(bankCardDto.getCvv())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .nameOnCard(bankCardDto.getNameOnCard())
                .expiringDate(bankCardDto.getExpiringDate())
                .build();
        bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard getBankCardByBankNumber(String bankNumber) throws IllegalStateException {
        return bankCardRepository.findCardByNumber(bankNumber)
                .orElseThrow(() -> new IllegalStateException("No BankCard found for bank number: " + bankNumber));

    }

    @Override
    public List<BankCard> findAll() {
        return bankCardRepository.findAll();
    }

    @Override
    public void removeBankCardForClient(Long bankCardId, Long clientId) {
        clientRepository.findById(clientId)
                .ifPresent(client -> {
                    Saving saving = client.getSaving();
                    saving.getBankCards()
                            .removeIf(bc -> Objects.equals(bc.getId(), bankCardId));
                });
    }
}
