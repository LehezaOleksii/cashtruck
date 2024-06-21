package com.projects.oleksii.leheza.cashtruck.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/auth")
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping(path = "/signup")
    public ModelAndView signUpMenu() {
        System.out.println("3333333");
        ModelAndView modelAndView = new ModelAndView("login/signup");
        return modelAndView;
    }

    @PostMapping(path = "/signup/send")
    public ModelAndView signUp() {
        System.out.println("444444");
        ModelAndView modelAndView = null;//client
        return modelAndView;
    }
}
