package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.google.gson.Gson;
import com.projects.oleksii.leheza.cashtruck.dto.payment.PaymentCreateRequest;
import com.projects.oleksii.leheza.cashtruck.dto.payment.StringReposne;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.exception.ErrorResponse;
import com.projects.oleksii.leheza.cashtruck.exception.PaymentException;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.SubscriptionService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentApiController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final Dotenv dotenv = Dotenv.load();
    private final String stripePublishableKey = dotenv.get("STRIPE_PUBLISHABLE_KEY");
    private final String endpointSecret = dotenv.get("STRIPE_WEBHOOK_SECRET");

    private final Gson gson = new Gson();

    @Operation(summary = "Get stripe publishable key", description = "Get string of stripe publishable key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Key returned successfully",
                    content = @Content(schema = @Schema(implementation = StringReposne.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/config")
    public ResponseEntity<StringReposne> getConfig() {
        StringReposne configResponse = new StringReposne(stripePublishableKey);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(configResponse);
    }

    @Operation(summary = "Create payment intent", description = "Create new payment intent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return client secret key of payment intent",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/create-payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentCreateRequest paymentCreateRequest) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentCreateRequest.getPrice() * 100)
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

    @Operation(summary = "Check is subscription exist", description = "Check is subscription exist by provided subscription status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get response of subscription exist",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "payment-request/is-valid")
    public ResponseEntity<Boolean> isSubscriptionStatusExistBySubscriptionStatus(@RequestBody SubscriptionStatus status) {
        Boolean isSubscriptionExist = subscriptionService.isSubscriptionStatusExistByStatus(status);
        return new ResponseEntity<>(isSubscriptionExist, isSubscriptionExist ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update user status", description = "Update user status from payment create request data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was update successfully ",
                    content = @Content(schema = @Schema(implementation = SubscriptionStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/update-user-status")
    public ResponseEntity<SubscriptionStatus> updateUserStatus(@RequestBody PaymentCreateRequest paymentCreateRequest) {
        log.info("update user plan. userId:{}, user plan:{}", paymentCreateRequest.getUserId(), paymentCreateRequest.getPrice());
        return new ResponseEntity<>(userService.updateUserPlan(paymentCreateRequest.getUserId(), SubscriptionStatus.valueOf(paymentCreateRequest.getSubscriptionPlan())), HttpStatus.OK);
    }

    @Operation(summary = "Handle Stripe payment", description = "Update user status in stripe webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status Update successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(path = "/stripe-payment-webhook")
    public ResponseEntity<String> handleStripePaymentWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        switch (event.getType()) {
            case "payment_intent.succeeded":
                System.out.println("💰 Payment received!");
                break;
            case "payment_intent.payment_failed":
                System.out.println("❌ Payment failed.");
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected event type");
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
