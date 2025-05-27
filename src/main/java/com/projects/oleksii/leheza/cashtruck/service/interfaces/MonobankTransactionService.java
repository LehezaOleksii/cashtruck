package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;

public interface MonobankTransactionService {

    void save(MonobankAccountTransactionDto monobankTransaction, Transaction transaction);
}
