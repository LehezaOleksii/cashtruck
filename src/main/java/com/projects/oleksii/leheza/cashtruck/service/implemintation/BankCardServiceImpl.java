package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.create.BankCardDto;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankCardServiceImpl implements BankCardService {

    private final BankCardRepository bankCardRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    private final CurrencyRepository currencyRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public BankCard save(BankCard bankCard) {
        return bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard save(BankCardDto bankCardDto) {
        Currency currency = currencyRepository.findByShortName(bankCardDto.getCurrencyShortName())
                .orElseThrow(() -> new ResourceNotFoundException("Currency with name: " + bankCardDto.getCurrencyShortName() + "not found"));
        BankCard bankCard = dtoMapper.bankCardDtoToBankCard(bankCardDto, currency);
        bankCard = bankCardRepository.save(bankCard);
        String firstTransactionName = "First transaction";
        BankTransaction bankTransaction = BankTransaction.builder()
                .currency(currency)
                .name(firstTransactionName)
                .sum((long) bankCardDto.getBalance()*currency.getDelimiter())
                .time(LocalDateTime.now())
                .build();
        bankTransactionRepository.save(bankTransaction);
        Category initialCategory = categoryRepository.findByName("Initial card balance")
                .orElseThrow(() -> new ResourceNotFoundException("Initial card balance not found"));
        Transaction firstTransaction = new Transaction();
        firstTransaction.setBankCard(bankCard);
        firstTransaction.setBankTransaction(bankTransaction);
        firstTransaction.setCategory(initialCategory);
        transactionRepository.save(firstTransaction);
        return bankCard;
    }

    @Override
    public BankCardDto getById(Long id) {
        return dtoMapper.bankCardToBankCardDto(bankCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bankcard with id:" + id + " was not found")));
    }

    @Override
    public List<BankCard> findAll() {
        return bankCardRepository.findAll();
    }

    @Override
    public boolean isClientHasCard(Long userId, String cardNumber) {
        if (userRepository.findById(userId).isPresent()) {
            return bankCardRepository.getBankCardsByUserId(userId)
                    .stream()
                    .anyMatch(card -> card.getCardNumber().equals(cardNumber));
        } else {
            throw new ResourceNotFoundException("User was not found when checking the existence of the card");
        }
    }

    @Override
    public boolean isClientHasCard(Long userId, Long cardId) {
        if (userRepository.findById(userId).isPresent()) {
            return bankCardRepository.getBankCardsByUserId(userId)
                    .stream()
                    .anyMatch(card -> card.getCardNumber().equals(bankCardRepository
                            .findById(cardId)
                            .orElseThrow(() -> new ResourceNotFoundException("Card with id:" + cardId + " does not found"))
                            .getCardNumber()));
        } else {
            throw new ResourceNotFoundException("User was not found when checking the existence of the card");
        }
    }

    @Override
    public void removeBankCardForClient(Long bankCardId, Long userId) {
        Optional<User> clientOptional = userRepository.findById(userId);
        if (clientOptional.isPresent()) {
            User client = clientOptional.get();
            client.getBankCards()
                    .removeIf(bc -> Objects.equals(bc.getId(), bankCardId));
            userRepository.save(client);
            log.info("remove bank card with id: {}", bankCardId);
        } else {
            log.warn("Error occurring bank card deleting, user with id {} does not exist ", userId);
            throw new ResourceNotFoundException("User has not exist");
        }
    }
}
