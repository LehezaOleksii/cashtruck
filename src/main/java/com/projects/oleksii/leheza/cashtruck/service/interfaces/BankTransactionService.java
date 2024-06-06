package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface BankTransactionService {

    BankTransaction saveTransaction(BankTransaction bankTransaction, LocalDateTime time);

    List<BankTransaction> findAll();

    List<BankTransaction> saveAll(List<BankTransaction> bankTransactions);

    BankTransaction save(BankTransaction bankTransaction);

//      List<Transaction> findTransactionsByClientId(Long clientId);
}
