package com.apd.userLoginRegisterEmailValidationSpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
