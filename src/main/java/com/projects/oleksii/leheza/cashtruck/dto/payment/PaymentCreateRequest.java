package com.projects.oleksii.leheza.cashtruck.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;
    private String email;
    @NotBlank(message = "Amount is required")
    private long amount;
    @NotBlank(message = "Card Number is required")
    private String cardNumber;
    private String dateValue;
    private String cvc;
    private String subscriptionStatus;
    private String paymentMethod;
}