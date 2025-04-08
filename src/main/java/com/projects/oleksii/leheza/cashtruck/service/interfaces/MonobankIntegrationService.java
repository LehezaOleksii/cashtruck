package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;

import java.util.Set;

public interface MonobankIntegrationService {

    void setMonobankToken(Long userId, String monobankToken);

    void saveMonobankAccountsAsBankCards(Long userId, Set<MonobankAccount> monobankAccounts);
}
