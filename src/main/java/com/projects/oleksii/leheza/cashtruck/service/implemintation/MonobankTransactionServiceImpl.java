package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankTransaction;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.repository.MonobankTransactionRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.MonobankTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonobankTransactionServiceImpl implements MonobankTransactionService {

    private final MonobankTransactionRepository monobankTransactionRepository;
    private final DtoMapper dtoMapper;

    @Override
    public void save(MonobankAccountTransactionDto monobankTransactionDto, Transaction transaction) {
        MonobankTransaction monobankTransaction = dtoMapper.monobankAccountTransactionDtoToMonobankTransaction(monobankTransactionDto);
        monobankTransaction.setTransaction(transaction);
        monobankTransactionRepository.save(monobankTransaction);
    }
}
