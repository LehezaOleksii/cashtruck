package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SubscriptionService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("clients/{clientId}/premium/payment_form")
    public ModelAndView paymentForm(@PathVariable("clientId") Long clientId,
                                    @RequestParam("plan") SubscriptionStatus status) {
        log.info("Is plan with name:{} exist in database ", status.name());
        if (subscriptionService.isSubscriptionStatusExistByStatus(status)) {
            ModelAndView modelAndView = new ModelAndView("client/payment");
            modelAndView.addObject("client", userService.getHeaderClientData(clientId));
            modelAndView.addObject("clientId", clientId);
            modelAndView.addObject("payment_data", subscriptionService.getPaymentCreateRequest(status));
            return modelAndView;
        } else {
            log.warn("Plan with name:{} does not exist, while payment data checking", status.name());
            return new ModelAndView("redirect:/clients/" + clientId + "/premium");
        }
    }

//    @PostMapping(path = "/update-user-status")
//    ModelAndView updateUserStatus(@RequestBody PaymentCreateRequest paymentCreateRequest) {
//        try {
//            PaymentIntent paymentIntent = paymentCreateRequest.getPaymentIntent();
//            paymentIntent.confirm();
//            if (paymentIntent.getStatus().equals(SUCCESS_STRIPE_PAYMENT_STATUS)) {
//                userService.updateUserPlan(paymentCreateRequest.getUserId(), SubscriptionStatus.valueOf(paymentCreateRequest.getSubscriptionPlan()));
//                log.info("update user plan. userId:{}, user plan:{}", paymentCreateRequest.getUserId(), paymentCreateRequest.getPrice());
//            } else {
//                log.warn("payment failed with status:{}", paymentIntent.getStatus());
//                throw new PaymentException("Payment failed");
//            }
//        } catch (StripeException e) {
//            throw new PaymentException(e.getMessage());
//        }
//        return new ModelAndView("redirect:/clients/" + paymentCreateRequest.getUserId() + "/premium");
//    }
}