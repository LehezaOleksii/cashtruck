package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.Confirmation;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.repository.ConfirmationRepository;
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
}
