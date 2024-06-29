package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.OtpRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.EmailService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Override
    public Boolean existByPassword(int password) {
        return otpRepository.existsByPassword(password);
    }

    @Override
    public User getUserByOtp(int password) {
        return otpRepository.findByPassword(password)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void deleteOtp(int password) {
        otpRepository.deleteByPassword(password);
    }

    @Override
    @Transactional
    public void sendOTP(String email) {
        if (otpRepository.existsByUserEmail(email)) {
           otpRepository.deleteByUserEmail(email);
        }
        emailService.sendOTP(email);
    }
}
