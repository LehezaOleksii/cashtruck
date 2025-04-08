package com.projects.oleksii.leheza.cashtruck.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonobankAccountDto {

    private String holderName;

    @JsonProperty("id")
    private String id;

    @JsonProperty("balance")
    private long balance;

    @JsonProperty("currencyCode")
    private int currencyCode;

    @JsonProperty("maskedPan")
    private List<String> maskedPan;

    @JsonProperty("type")
    private String type;

    @JsonProperty("cashbackType")
    private String currency;

    @JsonProperty("creditLimit")
    private long creditLimit;

    private int currencyDelimiter;
}

