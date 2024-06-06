package com.projects.oleksii.leheza.cashtruck.controllers;

import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthenticationController {


    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }



//    @PostMapping("/authenticate")
//    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//        final UserDetails user = userDao.findByUserEmail(request.getEmail());
//        if (user != null) {
//            return ResponseEntity.ok(jwtUtils.generateToken(user));
//
//        }
//        return ResponseEntity.status(400).body("Some error has occured");
//    }
}
