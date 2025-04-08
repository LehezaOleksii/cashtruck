package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankIntegration;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.BankCardRepository;
import com.projects.oleksii.leheza.cashtruck.repository.MonobankIntegrationRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.MonobankIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MonobankIntegrationServiceImpl implements MonobankIntegrationService {

    private final MonobankIntegrationRepository monobankIntegrationRepository;
    private final BankCardRepository bankCardRepository;
    private final DtoMapper dtoMapper;
    private final UserRepository userRepository;

    @Override
    public void setMonobankToken(Long userId, String monobankToken) {
        MonobankIntegration monobankIntegration = monobankIntegrationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Monobank Integration with user id:" + userId + " does not exist"));
        monobankIntegration.setMonobankToken(monobankToken);
        monobankIntegrationRepository.save(monobankIntegration);
    }

    @Override
    public void saveMonobankAccountsAsBankCards(Long userId, Set<MonobankAccount> monobankAccounts) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with user id:" + userId + " does not exist"));
        List<BankCard> userBankCards = bankCardRepository.getBankCardsByUserId(userId);
        List<BankCard> bankCards = monobankAccounts.stream()
                .map(monobankAccount -> dtoMapper.monobankAccountToBankCard(monobankAccount, user))
                .toList();
        bankCards.stream()
                .forEach(bankCard -> {
                    boolean isExists = false;
                    for (BankCard userBankCard : userBankCards) {
                        if (userBankCard.getCardNumber().equals(bankCard.getCardNumber())) {
                            isExists = true;
                        }
                    }
                    if (!isExists) {
                        bankCardRepository.save(bankCard);
                    }
                });
    }
}
