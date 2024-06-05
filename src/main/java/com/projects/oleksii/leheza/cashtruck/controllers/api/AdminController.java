package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admins", description = "Methods related to admins")
public class AdminController {

    private final UserService userService;

    @GetMapping(path = "/users/{userId}")
    ResponseEntity<Role> updateUserPlanStatus(@PathVariable("userId") Long userId,
                                              @RequestBody String status) {
        Role role = userService.updateUserRole(userId, Role.valueOf(status));
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
