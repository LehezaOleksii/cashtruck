package com.projects.oleksii.leheza.cashtruck.service.subscription;

import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.EmailService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@RequiredArgsConstructor
public class SubscriptionChecker {

    private final static String EMAIL_CONTEXT = "We wanted to inform you that your subscription to the Cash Truck service has ended. To continue enjoying all the features and benefits of our service, please update your subscription at your earliest convenience.";
    private final static String EMAIL_SUBJECT = "Cash Truck Subscription Expired";
    private final static String COMPANY_EMAIL = "leheza.oleksii@gmail.com";

    private final UserService userService;
    private final EmailService emailService;

    @Scheduled(cron = "@daily")
    public void checkAndUpdateSubscriptionStatus() {
        List<Long> userIds = userService.findUserIdsWithExpiredSubscriptions();
        for (Long id : userIds) {
            userService.updateUserPlan(id, SubscriptionStatus.FREE);
        }
        List<String> emails = userService.findUserEmailsWithExpiredSubscriptions();
        if (!emails.isEmpty()) {
            EmailContext emailContext = new EmailContext();
            for (String email : emails) {
                emailContext.setFrom(COMPANY_EMAIL);
                emailContext.setEmail(EMAIL_CONTEXT);
                emailContext.setSubject(EMAIL_SUBJECT);
                emailContext.setTo(email);
            }
            emailService.sendEmailWithAttachment(emailContext);
        }
    }
}
