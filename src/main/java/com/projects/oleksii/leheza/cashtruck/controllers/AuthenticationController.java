//package com.projects.oleksii.leheza.cashtruck.controllers;
//
//import com.projects.oleksii.leheza.cashtruck.config.JwtUtils;
//import com.projects.oleksii.leheza.cashtruck.dao.UserDao;
//import com.projects.oleksii.leheza.cashtruck.dto.AuthenticationRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("")
//@RequiredArgsConstructor
//public class AuthenticationController {
//
//
//    private final AuthenticationManager authenticationManager;
//    private final UserDao userDao;
//    private final JwtUtils jwtUtils;
//
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
//}
