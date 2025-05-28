package com.projects.oleksii.leheza.cashtruck.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonobankAccountTransactionDto {

    @JsonProperty("time")
    private Long time;
    @JsonProperty("description")
    private String description;
    @JsonProperty("mcc")
    private String mcc;
    @JsonProperty("operationAmount")
    private long amount;
    @JsonProperty("currencyCode")
    private String currencyCode;
    @JsonProperty("id")
    private String id;
}
