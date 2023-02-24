package com.apd.userLoginRegisterEmailValidationSpringBoot.repositories;

import com.apd.userLoginRegisterEmailValidationSpringBoot.models.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {

    //SDP spring data pattern
    Optional<ApplicationUser> findByEmail(String email);
}
