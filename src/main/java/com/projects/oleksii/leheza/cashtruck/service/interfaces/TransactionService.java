package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TransactionService {

      void saveTransaction(Transaction transaction);

      void saveTransaction(Transaction transaction, LocalDateTime time);

      List<Transaction> findAll();

      void saveAll(List<Transaction> transactions);

//      List<Transaction> findTransactionsByClientId(Long clientId);

}
