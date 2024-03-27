package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.auth.SignUpRequest;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("userForm", new SignUpRequest());
        return modelAndView;
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") @Valid SignUpRequest userForm, BindingResult bindingResult, ModelAndView model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        if (!userService.registerUser(userForm)) {
            model.addObject("usernameError", "User with this email has already exist");
            return "registration";
        }

        return "redirect:/clients";
    }
}