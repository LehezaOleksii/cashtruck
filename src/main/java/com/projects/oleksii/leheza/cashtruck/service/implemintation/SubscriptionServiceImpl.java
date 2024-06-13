package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Subscription;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.PaymentException;
import com.projects.oleksii.leheza.cashtruck.repository.SubscriptionRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final DtoMapper dtoMapper;

    @Override
    public Boolean isSubscriptionStatusExistByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findBySubscriptionStatus(status).isPresent();
    }

    @Override
    public PaymentCreateRequest getPaymentCreateRequest(SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findBySubscriptionStatus(status)
                .orElseThrow(() -> new PaymentException("payment with status: " + status.name() + " does not exist"));
        return dtoMapper.subscriptionToPaymentRequest(subscription).toBuilder().build();
    }
}
