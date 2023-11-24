package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/managers")
public class ManagerController {

    // TODO     @Resource
    private final ManagerService managerService;
    private final ClientService clientService;

    @PostMapping(path = "/create")
    ModelAndView createManager(@RequestBody ManagerDto managerDto) {
        ModelAndView modelAndView;
        if (managerService.findManagerByEmail(managerDto.getEmail()) != null) {
            modelAndView = new ModelAndView("redirect:/manager/create");
            modelAndView.addObject("manager", managerDto);
            //TODO error email is already taken
            return modelAndView;
        }
        managerService.saveManager(managerDto);
        modelAndView = new ModelAndView("manager/dashboard/{managerId}");
        return modelAndView;
    }

    @PutMapping(path = "/update/{managerId}")
    ModelAndView updateManagerInfo(@PathVariable("managerId") Long managerId, @ModelAttribute("manager") ManagerDto managerDto) {
        ModelAndView modelAndView = new ModelAndView();
        managerService.updateManagerInfo(managerId, managerDto);
        return modelAndView;
    }

    @DeleteMapping(path = "/{managerId}")
    ModelAndView deleteManager(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView();
        managerService.deleteManagerById(managerId);
        return modelAndView;
    }

    @GetMapping(path = "dashboard/{managerId}")
    ModelAndView showManagerDashboard(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("manager/dashboard");
        modelAndView.addObject("managerId", managerId);
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }

    @GetMapping(path = "/show/client/all")
    public ModelAndView showAllClients() {
        ModelAndView modelAndView = new ModelAndView("manager/show/client/all");
        modelAndView.addObject("clients", clientService.findAll());
        return modelAndView;
    }

    @GetMapping(path = "/show/client/{clientId}")
    public ModelAndView showClinetInfo(@PathVariable("clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("manager/show/client");
        modelAndView.addObject("client", clientService.getClient(clientId));
        return modelAndView;
    }
}
