package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final UserService userService;
    private final String stripeSecretKey = "rk_test_51PDMQi2N1Bginh2bRPPMW3fS7RhJoeblDjFNtWiWV8draNVtp6LMfzK7GsIvbXZ2wC9Po8klaqJmYXF3A3Rct6NL00P4vDv2Jf";
    private String stripePublishableKey = "pk_test_51PDMQi2N1Bginh2bUyYqHhWzip5F9uR8B2BFcGtBorVSDcThERpj5eNYK4gOqrGyUaoQ79aZdb8e0Lt7RfnLsFaM00Qhes8t5J";

    @PostMapping("clients/premium/payment_form/pay")
    public ResponseEntity<?> processPayment(
                                            @ModelAttribute PaymentCreateRequest paymentData) {
        Stripe.apiKey = stripeSecretKey;


        try {
            // Create payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentData.getAmount())
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Prepare response data
            HashMap<String, String> clientSecretResp = new HashMap<>();
            clientSecretResp.put("clientSecret", paymentIntent.getClientSecret());

            // Return success response
            return new ResponseEntity<>(clientSecretResp, HttpStatus.OK);

        } catch (StripeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/config")
    public ResponseEntity<String> getConfig() {
        return ResponseEntity.ok(stripePublishableKey);
    }

    @GetMapping("clients/{clientId}/premium/payment_form")
    public ModelAndView paymentForm(@PathVariable("clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("client/payment");
        modelAndView.addObject("client", userService.getHeaderClientData(clientId));
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("payment_request", new PaymentCreateRequest());
//        modelAndView.addObject("stripePublicKey", stripePublicKey);
        return modelAndView;
    }
}