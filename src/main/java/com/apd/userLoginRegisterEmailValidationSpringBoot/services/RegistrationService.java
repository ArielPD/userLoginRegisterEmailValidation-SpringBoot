package com.apd.userLoginRegisterEmailValidationSpringBoot.services;

import com.apd.userLoginRegisterEmailValidationSpringBoot.dto.RegistrationDto;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.ApplicationUser;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.Token;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.UserRole;
import com.apd.userLoginRegisterEmailValidationSpringBoot.repositories.TokenRepository;
import com.apd.userLoginRegisterEmailValidationSpringBoot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final String CONFIRMATION_URL = "";
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public String register(RegistrationDto registrationDto) {

        //check if the user already exits
        boolean userExits = repository.findByEmail(registrationDto.getEmail()).isPresent();
        if (userExits) {
            throw new IllegalStateException("A uer already exists with the same email");
        }

        // Encode the password
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());


        //transform - map the RegistationDto to ApplicationDto
        ApplicationUser aplicationUser = ApplicationUser.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        // Save the user
        ApplicationUser savedUser = repository.save(aplicationUser);

        // Generate a token
        String generatedToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(savedUser)
                .build();
        tokenRepository.save(token);

        // Send th confirmation email
        try {
            emailService.send(
                    registrationDto.getEmail(),
                    registrationDto.getFirstName(),
                    null,
                    CONFIRMATION_URL
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //return success message
        return generatedToken;
    }
}
