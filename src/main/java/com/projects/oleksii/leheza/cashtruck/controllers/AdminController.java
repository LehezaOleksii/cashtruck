package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/admins")
public class AdminController {

    private final UserService userService;

    @GetMapping(path = "/users/{userId}/role/update")
    ModelAndView updatePlanStatus(@PathVariable("userId") Long userId,
                                  @RequestParam("role") String status) {
        userService.updateUserRole(userId, Role.valueOf(status));
        return new ModelAndView("redirect:/managers/users");
    }
}