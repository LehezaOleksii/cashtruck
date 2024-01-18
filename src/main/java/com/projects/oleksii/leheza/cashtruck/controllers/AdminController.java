package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.dto.create.CreateAdminDto;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.AdminService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/admins")
public class AdminController {

    private final AdminService adminService;
    private final ClientService clientService;
    private final ManagerService managerService;

    @PostMapping(path = "/create/admin")
    ModelAndView createAdmin(@RequestBody CreateAdminDto createAdminDto) {
        ModelAndView modelAndView;
        if (adminService.findAdminByEmail(createAdminDto.getEmail()) != null) {
            modelAndView = new ModelAndView("redirect:/admin/create/admin");
            modelAndView.addObject("admin", createAdminDto);
            return modelAndView;
        }
        adminService.saveAdmin(createAdminDto);
        modelAndView = new ModelAndView("admin/dashboard");
        return modelAndView;
    }

    @PutMapping(path = "/change/admin/{adminId}")
    ModelAndView changeAdminInfo(@PathVariable("adminId") Long adminId, @RequestBody CreateAdminDto createAdminDto) {
        try {
            adminService.updateAdminInfo(adminId, createAdminDto);
            System.out.println("UPDATE admin");
        } catch (Exception e) {
            return new ModelAndView("admin/dashboard/{adminId}");
        }
        return new ModelAndView("redirect:/client/dashboard/{clientId}");
    }


    @GetMapping(path = "/show/client/all")
    public ModelAndView showAllClients() {
        ModelAndView modelAndView = new ModelAndView("admin/show/client/all");
        modelAndView.addObject("clients", clientService.findAll());
        return modelAndView;
    }

    @GetMapping(path = "/show/client/{clientId}")
    public ModelAndView showClinetInfo(@PathVariable("clientId") Long clientId) {
        ModelAndView modelAndView = new ModelAndView("admin/show/client");
        modelAndView.addObject("client", clientService.getClient(clientId));
        return modelAndView;
    }

    @GetMapping(path = "/show/manager/all")
    public ModelAndView showAllManager() {
        ModelAndView modelAndView = new ModelAndView("admin/show/manager/all");
        modelAndView.addObject("managers", managerService.findAllManagers());
        return modelAndView;
    }

    @GetMapping(path = "/show/manager/{managerId}")
    public ModelAndView showManagerInfo(@PathVariable("managerId") Long managerId) {
        ModelAndView modelAndView = new ModelAndView("admin/show/manager");
        modelAndView.addObject("manager", managerService.findManagerById(managerId));
        return modelAndView;
    }
}
