package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.google.gson.Gson;
import com.projects.oleksii.leheza.cashtruck.dto.payment.StringReposne;
import com.projects.oleksii.leheza.cashtruck.exception.PaymentException;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentApiController {

    private final UserService userService;
    private final Dotenv dotenv = Dotenv.load();
    private final String stripePublishableKey = dotenv.get("STRIPE_PUBLISHABLE_KEY");

    private final Gson gson = new Gson();

    @GetMapping(path = "/config")
    public ResponseEntity<StringReposne> getConfig() {
        StringReposne configResponse = new StringReposne(stripePublishableKey);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(configResponse);
    }

    @PostMapping(path = "/create-payment-intent")
    public ResponseEntity<String> createPaymentIntent() {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(11111L)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        PaymentIntent intent;
        try {
            intent = PaymentIntent.create(params);
        } catch (StripeException ex) {
            log.warn("Exception occurred while creating payment intent: {}", ex.getMessage());
            throw new PaymentException("Exception occurred while creating payment");
        }
        HashMap<String, String> clientSecretResp = new HashMap<>();
        clientSecretResp.put("clientSecret", intent.getClientSecret());
        return new ResponseEntity<>(gson.toJson(clientSecretResp), HttpStatus.OK);
    }
}
