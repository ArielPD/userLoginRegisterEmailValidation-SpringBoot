package com.apd.userLoginRegisterEmailValidationSpringBoot.services;

import com.apd.userLoginRegisterEmailValidationSpringBoot.dto.RegistrationDto;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.ApplicationUser;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.UserRole;
import com.apd.userLoginRegisterEmailValidationSpringBoot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository repository;

    public String register(RegistrationDto registrationDto) {

        //check if the user already exits
        boolean userExits = repository.findByEmail(registrationDto.getEmail()).isPresent();
        if (userExits) {
            throw new IllegalStateException("A uer already exists with the same email");
        }
        //transform - map the RegistationDto to ApplicationDto
        ApplicationUser aplicationUser = ApplicationUser.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .role(UserRole.ROLE_USER)
                .build();

        // Save the user
        repository.save(aplicationUser);

        //return success message
        return "User has been successfully created";
    }
}
