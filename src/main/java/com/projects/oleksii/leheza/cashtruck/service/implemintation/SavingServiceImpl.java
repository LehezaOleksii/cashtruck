package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.dto.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SavingRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final SavingRepository savingRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<Saving> findAll() {
        return savingRepository.findAll();
    }

    @Override
    public void saveAll(Collection<Saving> savings) {
        savingRepository.saveAll(savings);
    }

    @Override
    public void assignBankCardToClient(Long clientId, BankCard bankCard) throws IllegalStateException {
        if (!clientRepository.findById(clientId).isPresent()) {
            throw new IllegalStateException("Client dose not exist");
        }
        Client client = clientRepository.findById(clientId).get();
        Saving saving = client.getSaving();
        if (saving == null) {
            throw new IllegalStateException("Client does not have saving");
        }
        saving.getBankCards().add(bankCard);
        savingRepository.save(saving);
    }

    @Override
    public void assignBankCardsToClient(Long clientId, List<BankCard> bankCards) throws IllegalStateException {
        for (BankCard bankCard : bankCards) {
            assignBankCardToClient(clientId, bankCard);
        }
    }

    @Override
    public void assignBankCardDtoToClient(Long clientId, BankCardDto bankCardDto) {
        if (!clientRepository.findById(clientId).isPresent()) {
            throw new IllegalStateException("Client dose not exist");
        }
        Client client = clientRepository.findById(clientId).get();
        Saving saving = client.getSaving();
        if (saving == null) {
            throw new IllegalStateException("Client does not have saving");
        }
        BankCard bankCard = BankCard.builder()
                .cvv(bankCardDto.getCvv())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .nameOnCard(bankCardDto.getNameOnCard())
                .expiringDate(bankCardDto.getExpiringDate())
                .build();
        saving.getBankCards().add(bankCard);
        savingRepository.save(saving);
    }
}
