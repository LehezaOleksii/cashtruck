package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;

public interface SubscriptionService {

    Boolean isSubscriptionStatusExistByStatus(SubscriptionStatus status);

    PaymentCreateRequest getPaymentCreateRequest(SubscriptionStatus status);
}
