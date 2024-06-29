package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.User;

public interface OtpService {

    Boolean existByPassword(int password);

    User getUserByOtp(int password);

    void deleteOtp(int password);

    void sendOTP(String email);
}
