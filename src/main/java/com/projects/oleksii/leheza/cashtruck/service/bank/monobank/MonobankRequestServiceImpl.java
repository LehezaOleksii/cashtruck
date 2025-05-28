package com.projects.oleksii.leheza.cashtruck.service.bank.monobank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankAccountTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankClientInfoDto;
import com.projects.oleksii.leheza.cashtruck.dto.integration.MonobankRequestPersonalClientDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonobankRequestServiceImpl implements MonobankRequestService {

    private static final String KEY_ID = "3e6dac0b09f17f74e29b2ae3bb3174b950d009d5";

    private final HttpClient httpClient;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public MonobankClientInfoDto getMonobankClientInfo(String requestId) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException {
        String resourceUrl = "/personal/client-info";
        String xTime = String.valueOf(Instant.now().getEpochSecond());
        String xSign = generateXSignWithPrivateKey(xTime + requestId + resourceUrl);
        String url = "https://api.monobank.ua" + resourceUrl;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .header("X-Key-Id", KEY_ID)
                .header("X-Time", xTime)
                .header("X-Sign", xSign)
                .header("X-Request-Id", requestId)
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
    public List<MonobankAccountTransactionDto> getClientStatementInfo(String requestId, String monobankAccountId, Long lastTransactionTime) throws HttpTimeoutException, IOException, InterruptedException, HttpConnectTimeoutException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException {
        long timeMinus31Days = Instant.now().minus(31, ChronoUnit.DAYS).toEpochMilli();
        String resourceUrl = "/personal/statement/"+monobankAccountId+"/"+timeMinus31Days+"/"+lastTransactionTime;
        String xTime = String.valueOf(Instant.now().getEpochSecond());
        String xSign = generateXSignWithPrivateKey(xTime + requestId + resourceUrl);
        String url = "https://api.monobank.ua" + resourceUrl;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .header("X-Key-Id", KEY_ID)
                .header("X-Time", xTime)
                .header("X-Request-Id", requestId)
                .header("X-Sign", xSign)
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseClientTransactions(response.body());
        } else {
            log.warn("Failed to get Monobank client info. Status code: {}, Response: {}", response.statusCode(), response.body());
        }
        return List.of();
    }

    @Override
    public MonobankRequestPersonalClientDataDto requestToPersonalClientData(Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        String resourceUrl = "/personal/auth/request";
        String xTime = String.valueOf(Instant.now().getEpochSecond());
        String xSign = generateXSignWithPrivateKey(xTime + resourceUrl);
        String url = "https://api.monobank.ua" + resourceUrl;
//        String callback = "/webhook/monobank/userId/" + userId;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("X-Key-Id", KEY_ID)
                .header("X-Time", xTime)
                .header("X-Sign", xSign)
//                .header("X-Callback", callback)
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String tokenRequestId = jsonNode.get("tokenRequestId").asText();
            String acceptUrl = jsonNode.get("acceptUrl").asText();
            return new MonobankRequestPersonalClientDataDto(tokenRequestId, acceptUrl);
        } else {
            log.warn("Failed to get data while request to personal client data monobank. Status code: " + response.statusCode() + ". Response: " + response.body());
        }
        return new MonobankRequestPersonalClientDataDto();
    }

    @Override
    public boolean checkAccessToUserData(String requestId) {
        String resourceUrl = "/personal/auth/request";
        String xTime = String.valueOf(Instant.now().getEpochSecond());
        try {
            String xSign = generateXSignWithPrivateKey(xTime + requestId + resourceUrl);
            String url = "https://api.monobank.ua" + resourceUrl;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .GET()
                    .header("X-Key-Id", KEY_ID)
                    .header("X-Time", xTime)
                    .header("X-Request-Id", requestId)
                    .header("X-Sign", xSign)
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            } else {
                log.warn("Failed to get data while request to personal client data monobank. Status code: " + response.statusCode() + ". Response: " + response.body());
            }
        } catch (Exception e) {
            log.error("Error during check monobank access data. message: " + e.getMessage() + "; caused by: " + e.getCause());
        }
        return false;
    }

    public String generateXSignWithPrivateKey(String dataToSign) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("priv.key");
        if (is == null) {
            throw new java.io.FileNotFoundException("priv.key not found in resources.");
        }
        PemReader pemReader = new PemReader(new InputStreamReader(is));
        PemObject pemObject = pemReader.readPemObject();
        pemReader.close();

        byte[] keyBytes = pemObject.getContent();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signer = Signature.getInstance("SHA256withECDSA", "BC");
        signer.initSign(privateKey);
        signer.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        byte[] derSignature = signer.sign();

        return Base64.getEncoder().encodeToString(derSignature);
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
