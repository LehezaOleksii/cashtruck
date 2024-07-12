package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.Confirmation;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.repository.ConfirmationRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.OtpService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ConfirmationRepository confirmationRepository;
    private final OtpService otpService;

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping(path = "/signup")
    public ModelAndView signUpMenu() {
        ModelAndView modelAndView = new ModelAndView("login/signup");
        modelAndView.addObject("login", new LoginDto());
        return modelAndView;
    }

    @PostMapping(path = "/register")
    public ModelAndView signUp(@ModelAttribute("login") LoginDto loginDto) {
        userService.saveNewUser(loginDto);
        return new ModelAndView("redirect:/auth/authorization_complete");
    }

    @GetMapping(path = "/authorization_complete")
    public ModelAndView authorizationCompleteMenu() {
        return new ModelAndView("login/authorization_complete");
    }

    @GetMapping(path = "/users")
    public ModelAndView confirmEmail(@RequestParam String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        userService.setStatus(confirmation.getUser().getId(), ActiveStatus.ACTIVE);
        confirmationRepository.delete(confirmation);
        return new ModelAndView("login/login");
    }

    @GetMapping(path = "/forgot-password")
    public ModelAndView forgotPasswordGetEmail() {
        return new ModelAndView("login/forgot-password-email");
    }

    @PostMapping(path = "/forgot-password/send-otp")
    public ModelAndView forgotPasswordSendOtp(@RequestParam String email) {
        ModelAndView modelAndView;
        if (userService.existByEmail(email)) {
            otpService.sendOTP(email);
            modelAndView = new ModelAndView("login/forgot-password-otp");
            modelAndView.addObject("email", email);
        } else {
            modelAndView = new ModelAndView("login/signup");
        }
        return modelAndView;
    }

    @PostMapping(path = "/verify-otp")
    public ModelAndView verifyOtp(@RequestParam("otp") String otp, @RequestParam String email) {
        int otpInt = Integer.parseInt(otp);
        ModelAndView modelAndView;
        if (otpService.existByPassword(otpInt)) {
            otpService.deleteOtp(otpInt);
            modelAndView = new ModelAndView("login/change-password");
            modelAndView.addObject("email", email);
        } else {
            modelAndView = new ModelAndView("login/forgot-password-otp");
            modelAndView.addObject("email", email);
            modelAndView.addObject("errorMessage", "Incorrect password. Please try again.");
        }
        return modelAndView;
    }


    @PostMapping(path = "/change-password")
    public ModelAndView changePassword(@ModelAttribute("email") String email, String newPassword) {
        userService.setNewPassword(email, newPassword);
        return new ModelAndView("login/login");
    }

    @GetMapping(path = "/resend-otp")
    public ModelAndView resendOtp(@RequestParam String email) {
        ModelAndView modelAndView;
        if (userService.existByEmail(email)) {
            otpService.sendOTP(email);
            modelAndView = new ModelAndView("login/forgot-password-otp");
            modelAndView.addObject("email", email);
        } else {
            modelAndView = new ModelAndView("login/login");
        }
        return modelAndView;
    }
}