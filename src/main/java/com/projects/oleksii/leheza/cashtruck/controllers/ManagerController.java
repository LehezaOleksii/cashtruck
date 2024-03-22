package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.service.email.EmailServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/managers")
public class ManagerController {

    // TODO     @Resource
    private final UserService userService;
    private final ImageConvertor imageConvertor;
    private final EmailServiceImpl emailService;

    @PutMapping(path = "/update/{userId}")
    ModelAndView updateManagerInfo(@PathVariable("userId") Long managerId, @ModelAttribute("manager") UserUpdateDto userUpdateDto) {
        ModelAndView modelAndView = new ModelAndView();
//        userService.updateUserInfo(managerId, userUpdateDto);
        return modelAndView;
    }

//    @GetMapping(path = "/{managerId}")
//    ModelAndView showManagerDashboard(@PathVariable("managerId") Long managerId) {
//        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
//        modelAndView.addObject("managerId", managerId);
////        modelAndView.addObject("manager", userService.(managerId));
//        return modelAndView;
//    }

    @GetMapping(path = "/{managerId}/users")
    ModelAndView getClientsList(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/users");
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("users", userService.findAll());
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{userId}")
    ModelAndView getClientsList(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        //if user == client modelAndView
        //else another modelAndView
        ModelAndView modelAndView = new ModelAndView("manager/client_info");
        modelAndView.addObject("manager", userService.getUserDto(managerId));


//        ModelAndView modelAndView = new ModelAndView("manager/manager_info");
//        modelAndView.addObject("user", managerService.findManagerById(managerId).getCustomUser());
//        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{clientId}/profile")
    ModelAndView getClientProfileForm(@PathVariable("managerId") Long managerId, @PathVariable("clientId") Long clientId) {
        //if user == client modelAndView
        //else another modelAndView
        ModelAndView modelAndView = new ModelAndView("manager/client_profile");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("clientDto", userService.getClientUpdateDto(clientId));


//        ModelAndView modelAndView = new ModelAndView("manager/manager_info");
//        modelAndView.addObject("user", managerService.findManagerById(managerId).getCustomUser());
//        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/users/{clientId}/profile")
    ModelAndView changeClientProfile(@PathVariable Long managerId, @PathVariable Long clientId, @Valid @ModelAttribute("clientDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("manager/client_profile")
                    .addObject("manager", userService.getHeaderClientData(clientId))
                    .addObject("clientId", clientId)
                    .addObject("managerId", managerId);

        } else {
            if (!avatar.isEmpty()) {
                try {
                    userUpdateDto.setAvatar(imageConvertor.convertByteImageToString(avatar.getBytes()));
                } catch (IOException e) {
                    return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId)
                            .addObject("errorMessage", e.getMessage());
                }
            }
            try {
                userService.updateUserInfo(clientId, userUpdateDto);
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId + "/profile");
                modelAndView.addObject("client", userService.getHeaderClientData(clientId));
                return modelAndView.addObject("errorMessage", e.getMessage());
            }
            return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId);
        }
    }

    @GetMapping(path = "/{managerId}/plans")
    ModelAndView getPlansModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/plans");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/plans/{planId}")
    ModelAndView getPlanModerationMenu(@PathVariable("managerId") Long managerId, @PathVariable("planId") Long planId) {
        ModelAndView modelAndView = new ModelAndView("manager/plan");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }


    @GetMapping(path = "/{managerId}/plans/create")
    ModelAndView createPlanModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_plan");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/emails")
    ModelAndView getEmailsMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/emails/send")
    ModelAndView sendEmail(@PathVariable("managerId") Long managerId,
                           @Valid @ModelAttribute("email") EmailContext email) {
        emailService.sendEmailWithAttachment(userService.getUserById(managerId).getEmail(), email);
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", userService.getUserDto(managerId));
        modelAndView.addObject("email", new EmailContext());
        return new ModelAndView("redirect:/managers/" + managerId + "/emails");
    }
}
