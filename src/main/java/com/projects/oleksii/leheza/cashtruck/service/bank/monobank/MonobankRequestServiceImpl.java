package com.projects.oleksii.leheza.cashtruck.service.bank.monobank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankClientInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonobankRequestServiceImpl implements MonobankRequestService {

    private final HttpClient httpClient;

    @Override
    public MonobankClientInfoDto getMonobankClientInfo(String monobankToken) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.monobank.ua/personal/client-info"))
                .GET()
                .header("X-Token", monobankToken)
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseMonobankClientInfo(response.body());
        } else {
            log.warn("Failed to get Monobank client info. Status code: {}, Response: {}", response.statusCode(), response.body());
        }
        return null;
    }

    @Override
    public List<MonobankAccountTransactionDto> getClientStatementInfo(String monobankToken, String monobankAccountId, Long lastTransactionTime) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException {

        long timeMinus31Days = Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.monobank.ua/personal/statement/" + monobankAccountId + "/" + timeMinus31Days + "/" + lastTransactionTime))
                .GET()
                .header("X-Token", monobankToken)
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseClientTransactions(response.body());
        } else {
            log.warn("Failed to get Monobank client info. Status code: {}, Response: {}", response.statusCode(), response.body());
        }
        return null;
    }

    private MonobankClientInfoDto parseMonobankClientInfo(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MonobankClientInfoDto monobankClientInfoDto = objectMapper.readValue(responseBody, MonobankClientInfoDto.class);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String name = rootNode.path("name").asText();
            monobankClientInfoDto.getAccounts().forEach(account -> {
                account.setHolderName(name);
            });
            return monobankClientInfoDto;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Monobank response: {}", e.getMessage());
            return null;
        }
    }

    private List<MonobankAccountTransactionDto> parseClientTransactions(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Monobank response: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
