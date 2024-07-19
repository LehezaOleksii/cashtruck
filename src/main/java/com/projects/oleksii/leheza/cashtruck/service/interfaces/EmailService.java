package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;

public interface EmailService {

    void sendEmailWithAttachment(EmailContext emailContext);

    void sendConformationEmailRequest(String to, String token);

    String getEmailMessage(String host, String token);

    String getVerificationUrl(String host, String token);

    void sendOTP(String email);
}
