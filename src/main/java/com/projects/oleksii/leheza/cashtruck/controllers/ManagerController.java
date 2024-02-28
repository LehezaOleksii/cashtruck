package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.update.ClientUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ManagerUpdateDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.CustomUserService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
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
    private final ManagerService managerService;
    private final CustomUserService customUserService;
    private final ClientService clientService;
    private final ImageConvertor imageConvertor;

    @PutMapping(path = "/update/{managerId}")
    ModelAndView updateManagerInfo(@PathVariable("managerId") Long managerId, @ModelAttribute("manager") ManagerUpdateDto managerUpdateDto) {
        ModelAndView modelAndView = new ModelAndView();
        managerService.updateManagerInfo(managerId, managerUpdateDto);
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}")
    ModelAndView showManagerDashboard(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users")
    ModelAndView getClientsList(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/users");
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        modelAndView.addObject("users", customUserService.findAll());
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/users/{userId}")
    ModelAndView getClientsList(@PathVariable("managerId") Long managerId, @PathVariable("userId") Long userId) {
        //if user == client modelAndView
        //else another modelAndView
        ModelAndView modelAndView = new ModelAndView("manager/client_info");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));


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
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        modelAndView.addObject("clientDto", clientService.getClientUpdateDto(clientId));


//        ModelAndView modelAndView = new ModelAndView("manager/manager_info");
//        modelAndView.addObject("user", managerService.findManagerById(managerId).getCustomUser());
//        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @PostMapping(path = "/{managerId}/users/{clientId}/profile")
    ModelAndView changeClientProfile(@PathVariable Long managerId, @PathVariable Long clientId, @Valid @ModelAttribute("clientDto") ClientUpdateDto clientUpdateDto, BindingResult bindingResult, @RequestParam("image") MultipartFile avatar) {
        if (bindingResult.hasFieldErrors()) {
            return new ModelAndView("manager/client_profile")
                    .addObject("manager", clientService.getHeaderClientData(clientId))
                    .addObject("clientId", clientId)
                    .addObject("managerId", managerId);

        } else {
            if (!avatar.isEmpty()) {
                try {
                    clientUpdateDto.setAvatar(imageConvertor.convertByteImageToString(avatar.getBytes()));
                } catch (IOException e) {
                    return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId)
                            .addObject("errorMessage", e.getMessage());
                }
            }
            try {
                clientService.updateClientInfo(clientId, clientUpdateDto);
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId + "/profile");
                modelAndView.addObject("client", clientService.getHeaderClientData(clientId));
                return modelAndView.addObject("errorMessage", e.getMessage());
            }
            return new ModelAndView("redirect:/managers/" + managerId + "/users/" + clientId);
        }
    }

    @GetMapping(path = "/{managerId}/plans")
    ModelAndView getPlansModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/plans");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/{managerId}/plans/{planId}")
    ModelAndView getPlanModerationMenu(@PathVariable("managerId") Long managerId,@PathVariable("planId") Long planId) {
        ModelAndView modelAndView = new ModelAndView("manager/plan");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }


    @GetMapping(path = "/{managerId}/plans/create")
    ModelAndView createPlanModerationMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/create_plan");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }
    @GetMapping(path = "/{managerId}/emails")
    ModelAndView getEmailsMenu(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/emails");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

}
