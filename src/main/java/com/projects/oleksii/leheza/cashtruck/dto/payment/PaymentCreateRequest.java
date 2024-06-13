package com.projects.oleksii.leheza.cashtruck.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {

    private Long userId;
    @NotNull
    @NotBlank(message = "Amount is required")
    private Long price;
    private String subscriptionPlan;
}