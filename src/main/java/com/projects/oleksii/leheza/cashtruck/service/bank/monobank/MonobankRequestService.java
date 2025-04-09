package com.projects.oleksii.leheza.cashtruck.service.bank.monobank;

import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankClientInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankRequestPersonalClientDataDto;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface MonobankRequestService {

    MonobankClientInfoDto getMonobankClientInfo(String requestId) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException;

    List<MonobankAccountTransactionDto> getClientStatementInfo(String requestId, String monobankAccountId, Long lastTransactionTime) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException;

    MonobankRequestPersonalClientDataDto requestToPersonalClientData(Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException, InterruptedException;

    boolean checkAccessToUserData(String requestId);
}
