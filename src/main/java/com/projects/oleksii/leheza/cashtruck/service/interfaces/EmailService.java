package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;

public interface EmailService {

    void sendEmailWithAttachment(EmailContext emailContext);

    void sendConformationEmailRequest(String to, String token);

    String getEmailMessage(String name, String host, String token);

    String getVerificationUrl(String host, String token);
}
