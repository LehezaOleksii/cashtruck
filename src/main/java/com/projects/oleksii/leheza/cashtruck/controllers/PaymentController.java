package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SubscriptionService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("clients/premium/payment_form")
    public ModelAndView paymentForm(@RequestParam("plan") SubscriptionStatus status,
                                    @AuthenticationPrincipal User user) {
        log.info("Is plan with name:{} exist in database ", status.name());
        Long clientId = user.getId();
        if (subscriptionService.isSubscriptionStatusExistByStatus(status)) {
            ModelAndView modelAndView = new ModelAndView("client/payment");
            modelAndView.addObject("client", userService.getHeaderClientData(clientId));
            modelAndView.addObject("clientId", clientId);
            modelAndView.addObject("payment_data", subscriptionService.getPaymentCreateRequest(status));
            return modelAndView;
        } else {
            log.warn("Plan with name:{} does not exist, while payment data checking", status.name());
            return new ModelAndView("redirect:/clients/premium");
        }
    }
}