package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.BankTransaction;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
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
    public void save(BankTransaction bankTransaction) { //TODO transaction logic
        bankTransactionRepository.save(bankTransaction);
    }

    @Override
    public void saveTransaction(BankTransaction bankTransaction, LocalDateTime time) {
        bankTransaction.setTime(time);
        bankTransactionRepository.save(bankTransaction);
    }

    @Override
    public List<BankTransaction> findAll() {
        return bankTransactionRepository.findAll();
    }

    @Override
    public void saveAll(List<BankTransaction> bankTransactions) {
        bankTransactionRepository.saveAll(bankTransactions);
    }
}
