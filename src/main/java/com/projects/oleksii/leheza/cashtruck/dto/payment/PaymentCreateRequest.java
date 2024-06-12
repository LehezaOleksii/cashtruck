package com.projects.oleksii.leheza.cashtruck.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {

    @NotNull
    @NotBlank(message = "Amount is required")
    private long amount;
    private String subscriptionStatus;
}