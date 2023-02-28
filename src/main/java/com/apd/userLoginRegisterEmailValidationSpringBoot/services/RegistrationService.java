package com.apd.userLoginRegisterEmailValidationSpringBoot.services;

import com.apd.userLoginRegisterEmailValidationSpringBoot.dto.RegistrationDto;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.ApplicationUser;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.Token;
import com.apd.userLoginRegisterEmailValidationSpringBoot.models.UserRole;
import com.apd.userLoginRegisterEmailValidationSpringBoot.repositories.TokenRepository;
import com.apd.userLoginRegisterEmailValidationSpringBoot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final String CONFIRMATION_URL = "http://localhost:8080/api/v1/authentication/confirm?token=%s";
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

        // Send the confirmation email
        try {
            emailService.send(
                    registrationDto.getEmail(),
                    registrationDto.getFirstName(),
                    null,
                    String.format(CONFIRMATION_URL, generatedToken)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //return success message
        return generatedToken;
    }

    public String confirm(String token) {

        // get the token
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found."));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {

            // Generate a token
            String generateToken = UUID.randomUUID().toString();
            Token newToken = Token.builder()
                    .token(generateToken)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .user(savedToken.getUser())
                    .build();
            tokenRepository.save(newToken);

            // Send the confirmation email
            try {
                emailService.send(
                        savedToken.getUser().getEmail(),
                        savedToken.getUser().getFirstName(),
                        null,
                        String.format(CONFIRMATION_URL, generateToken)
                );
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return "Token expired, a new token has been sent to your email";
        }

        ApplicationUser user = repository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        repository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        return "<h1>Your account has been successfully activated</h1>";
    }
}
