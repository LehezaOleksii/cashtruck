package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.repository.BankCardRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SavingRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final SavingRepository savingRepository;
    private final UserRepository userRepository;
    private final BankCardRepository bankCardRepository;


    @Override
    public List<Saving> findAll() {
        return savingRepository.findAll();
    }

    @Override
    public void saveAll(Collection<Saving> savings) {
        savingRepository.saveAll(savings);
    }

    @Override
    public void assignBankCardToClient(Long userId, BankCard bankCard) throws IllegalArgumentException {
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalStateException("Client dose not exist");
        }
        User user = userRepository.findById(userId).get();
        Saving saving = user.getSaving();
        if (saving.getBankCards().stream()
                .anyMatch(bc -> bc.getCardNumber().equals(bankCard.getCardNumber()))) {
        }
        if (saving.getBankCards().size()+1 > user.getSubscription().getMaxCardsSupport()) {
            throw new IllegalArgumentException("Client plan does not maintain this functionality");
        }
        saving.getBankCards().add(bankCard);
        savingRepository.save(saving);
    }

    @Override //TODO delete
    public void assignBankCardsToClient(Long userId, List<BankCard> bankCards) throws IllegalStateException {
        for (BankCard bankCard : bankCards) {
            assignBankCardToClient(userId, bankCard);
        }
    }

    @Override
    public void assignBankCardDtoToClient(Long userId, CreateBankCardDto bankCardDto) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalStateException("Client dose not exist");
        }
        User user = userRepository.findById(userId).get();
        Saving saving = user.getSaving();
        if (saving.getBankCards().size() > user.getSubscription().getMaxCardsSupport()) {
            throw new IllegalStateException("Client plan does not maintain this functionality");
        }
        BankCard bankCard = BankCard.builder()
                .cvv(bankCardDto.getCvv())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .nameOnCard(bankCardDto.getNameOnCard())
                .expiringDate(bankCardDto.getExpiringDate())
                .build();
        bankCardRepository.save(bankCard);
        assignBankCardToClient(userId, bankCard);
    }
}
