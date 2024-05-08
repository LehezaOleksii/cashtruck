package com.projects.oleksii.leheza.cashtruck.service.implemintation;


import com.projects.oleksii.leheza.cashtruck.domain.Payment;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentResponseDto;
import com.projects.oleksii.leheza.cashtruck.repository.PaymentRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.PaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponseDto createPayment(PaymentCreateRequest paymentRequestDTO) {
        Payment payment = convertToEntity(paymentRequestDTO);
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponseDTO(savedPayment);
    }

    private Payment convertToEntity(PaymentCreateRequest paymentRequestDTO) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentRequestDTO, payment);
        return payment;
    }

    private PaymentResponseDto convertToResponseDTO(Payment payment) {
        PaymentResponseDto responseDTO = new PaymentResponseDto();
        BeanUtils.copyProperties(payment, responseDTO);
        return responseDTO;
    }
}