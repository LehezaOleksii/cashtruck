package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateBankCardDto;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.BankCardRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {

    private final BankCardRepository bankCardRepository;
    private final UserRepository userRepository;


    @Override
    public void save(BankCard bankCard) {
        bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard save(CreateBankCardDto bankCardDto) {
        BankCard bankCard = BankCard.builder()
                .cvv(bankCardDto.getCvv())
                .bankName(bankCardDto.getBankName())
                .cardNumber(bankCardDto.getCardNumber())
                .nameOnCard(bankCardDto.getNameOnCard())
                .balance(BigDecimal.valueOf(bankCardDto.getBalance()))
                .expiringDate(bankCardDto.getExpiringDate())
                .build();
        if (bankCardDto.getId() != null) {
            bankCard.setId(bankCardDto.getId());
        }
        return bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard getBankCardByBankNumber(String bankNumber) {
        return bankCardRepository.findCardByNumber(bankNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bankcard with number:" + bankNumber + " was not found"));
    }

    @Override
    public BankCard getById(Long id) {
        return bankCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bankcard with id:" + id + " was not found"));
    }

    @Override
    public List<BankCard> findAll() {
        return bankCardRepository.findAll();
    }

    @Override
    public boolean isClientHasCard(Long userId, CreateBankCardDto bankCardDto) {
        if (userRepository.findById(userId).isPresent()) {
            return bankCardRepository.getBankCardsByUserId(userId)
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(card -> card.getCardNumber().equals(bankCardDto.getCardNumber()));
        } else {
            throw new ResourceNotFoundException("User was not found when checking the existence of the card");
        }
    }

    @Override
    public void removeBankCardForClient(Long bankCardId, Long userId) {
        Optional<User> clientOptional = userRepository.findById(userId);
        if (clientOptional.isPresent()) {
            User client = clientOptional.get();
            Saving saving = client.getSaving();
            saving.getBankCards()
                    .removeIf(bc -> Objects.equals(bc.getId(), bankCardId));
            userRepository.save(client);
        } else {
            throw new ResourceNotFoundException("User has not exist");
        }
    }
}
