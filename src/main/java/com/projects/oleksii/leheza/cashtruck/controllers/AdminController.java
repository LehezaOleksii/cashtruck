package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/admins")
public class AdminController {

    private final UserService userService;

//    @GetMapping(path = "/{adminId}/users/{userId}")
//    ResponseEntity<?> updateUserPlanStatus(@PathVariable("adminId") Long adminId, @PathVariable("userId") Long userId,
//                                        @RequestParam("role") String status) {
//        userService.updateUserRole(userId, Role.valueOf(status));
//        return new ResponseEntity<>();
//    }
}