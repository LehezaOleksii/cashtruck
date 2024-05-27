package com.projects.oleksii.leheza.cashtruck.service.subscription;

import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class SubscriptionChecker {

    private final UserService userService;

    public SubscriptionChecker(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "@daily")
    public void checkAndUpdateSubscriptionStatus() {
        List<Long> userIds = userService.findUserIdsWithExpiredSubscriptions();
        for (Long id : userIds) {
            userService.updateUserPlan(id, SubscriptionStatus.FREE);
        }
    }
}
