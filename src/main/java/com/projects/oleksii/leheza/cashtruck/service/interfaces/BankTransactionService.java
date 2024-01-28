package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankTransaction;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface BankTransactionService {

    void saveTransaction(BankTransaction bankTransaction, LocalDateTime time);

    List<BankTransaction> findAll();

    void saveAll(List<BankTransaction> bankTransactions);

    void save(BankTransaction bankTransaction);

//      List<Transaction> findTransactionsByClientId(Long clientId);
}
