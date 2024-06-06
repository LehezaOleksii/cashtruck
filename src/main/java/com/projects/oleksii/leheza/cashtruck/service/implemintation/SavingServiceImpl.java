package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.exception.UserPlanException;
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
    public Collection<Saving> saveAll(Collection<Saving> savings) {
        return savingRepository.saveAll(savings);
    }

    @Override
    public void assignBankCardToClient(Long userId, BankCard bankCard) throws IllegalArgumentException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " does not exist"));
        Saving saving = user.getSaving();
        saving.getBankCards().stream()
                .anyMatch(bc -> bc.getCardNumber().equals(bankCard.getCardNumber()));
        if (saving.getBankCards().size() + 1 > user.getSubscription().getMaxCardsSupport()) {
            throw new UserPlanException("Client plan does not maintain this functionality");
        }
        saving.getBankCards().add(bankCard);
        savingRepository.save(saving);
    }
}
