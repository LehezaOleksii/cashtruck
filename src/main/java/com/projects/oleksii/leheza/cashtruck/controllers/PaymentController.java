package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final UserService userService;
    private final String stripeSecretKey ="sk_test_51PDMQi2N1Bginh2bBeJFhV71c83rF7vRIu5Xd8n2EYPmzPEakaY36JJS8FfqXIasMr2jgXxgXuBw68Zzg0sfdcPD00C0frlvmp";

    String paymentIntentId = "pi_your_payment_intent_id";

    @PostMapping("clients/{clientId}/premium/payment_form/pay")
    public ModelAndView processPayment(@PathVariable("clientId") Long clientId,
                                       @ModelAttribute PaymentCreateRequest paymentData) {
        Stripe.apiKey = stripeSecretKey;
        ModelAndView modelAndView = new ModelAndView()
                .addObject("client", userService.getHeaderClientData(clientId))
                .addObject("clientId", clientId);
        try {
            // Create payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCurrency("usd")
                    .setAmount(paymentData.getAmount())
                    .setPaymentMethod("pm_card_visa")
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Handle successful payment
            System.out.println(paymentIntent.getStatus());
            if ("succeeded".equals(paymentIntent.getStatus())) {
                userService.updateUserPlan(clientId, SubscriptionStatus.valueOf(paymentData.getSubscriptionStatus()));
                System.out.println("1111111111111111111");
                modelAndView.setViewName("redirect:/clients/"+clientId+"/premium");
                return modelAndView;
            } else {
                System.out.println("22222222222222");
                // Payment failed
                modelAndView.setViewName("redirect:/clients/"+clientId+"/premium");
                return modelAndView;
            }
        } catch (StripeException e) {
            System.out.println("33333333333333333"+ e.getMessage());
            modelAndView.setViewName("redirect:/clients/"+clientId+"/premium");
            return modelAndView;
        }
    }
}