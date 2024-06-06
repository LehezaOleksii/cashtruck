package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentCreateRequest paymentRequestDTO);
}
