//package com.projects.oleksii.leheza.cashtruck.controllers;
//
//import com.projects.oleksii.leheza.cashtruck.service.interfaces.MonobankIntegrationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@RequestMapping(path = "/webhook")
//@RequiredArgsConstructor
//@Slf4j
//public class WebhookController {
//
//    private final MonobankIntegrationService monobankIntegrationService;
//
//    @GetMapping("/monobank/userId/{userId}")
//    public ResponseEntity<Void> setMonobankRequestId(
//            @PathVariable Long userId,
//            @RequestHeader("X-Request-Id") String tokenRequestId) {
//        monobankIntegrationService.setMonobankRequestId(userId, tokenRequestId);
//        return ResponseEntity.ok().build();
//    }
//}
