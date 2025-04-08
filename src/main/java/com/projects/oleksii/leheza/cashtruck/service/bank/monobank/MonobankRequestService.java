package com.projects.oleksii.leheza.cashtruck.service.bank.monobank;

import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankClientInfoDto;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.List;

public interface MonobankRequestService {

    MonobankClientInfoDto getMonobankClientInfo(String monobankToken) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException;

    List<MonobankAccountTransactionDto> getClientStatementInfo(String monobankToken, String monobankAccountId, Long lastTransactionTime) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException;
}
