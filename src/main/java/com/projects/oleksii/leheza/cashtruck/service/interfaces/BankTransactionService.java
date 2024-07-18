package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankTransaction;

import java.util.List;

public interface BankTransactionService {

    List<BankTransaction> findAll();

    List<BankTransaction> saveAll(List<BankTransaction> bankTransactions);

    BankTransaction save(BankTransaction bankTransaction);
}
