package com.example.sbbackend.repositories;

import com.example.sbbackend.dto.Role;
import com.example.sbbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>findByEmail(String email);
    Optional<User> findByPasswordToken(String resetToken);
    Optional<User> findByConfirmationToken(String confirmationToken);
    Optional<User> findByRole(Role role);


}
