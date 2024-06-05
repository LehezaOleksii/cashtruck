package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankTransaction;
import com.projects.oleksii.leheza.cashtruck.repository.BankTransactionRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.BankTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl implements BankTransactionService {

    private final BankTransactionRepository bankTransactionRepository;

    @Override
    public BankTransaction save(BankTransaction bankTransaction) { //TODO transaction logic
        return bankTransactionRepository.save(bankTransaction);
    }

    @Override
    public BankTransaction saveTransaction(BankTransaction bankTransaction, LocalDateTime time) {
        bankTransaction.setTime(time);
        return bankTransactionRepository.save(bankTransaction);
    }

    @Override
    public List<BankTransaction> findAll() {
        return bankTransactionRepository.findAll();
    }

    @Override
    public List<BankTransaction> saveAll(List<BankTransaction> bankTransactions) {
        return bankTransactionRepository.saveAll(bankTransactions);
    }
}
