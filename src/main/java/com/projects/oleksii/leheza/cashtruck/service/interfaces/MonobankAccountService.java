package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.monobank.MonobankAccount;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountDto;

import java.util.List;

public interface MonobankAccountService {

    void saveMonobankAccounts(Long userId, List<MonobankAccountDto> accountsDto);

    List<MonobankAccountDto> findByUserId(Long userId);

    List<MonobankAccount> findByPans(List<String> pans);
}
