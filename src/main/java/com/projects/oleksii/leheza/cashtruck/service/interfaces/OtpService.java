package com.projects.oleksii.leheza.cashtruck.service.interfaces;

public interface OtpService {

    Boolean existByPassword(int password);

    void deleteOtp(int password);

    void sendOTP(String email);
}
