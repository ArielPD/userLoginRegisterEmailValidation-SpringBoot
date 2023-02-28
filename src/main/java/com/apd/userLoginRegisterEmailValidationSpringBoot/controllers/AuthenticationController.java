package com.apd.userLoginRegisterEmailValidationSpringBoot.controllers;

import com.apd.userLoginRegisterEmailValidationSpringBoot.dto.RegistrationDto;
import com.apd.userLoginRegisterEmailValidationSpringBoot.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDto registrationDto) {
        return ResponseEntity.ok(service.register(registrationDto));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token) {
        return ResponseEntity.ok(service.confirm(token));
    }
}
