package com.example.sbbackend.config;

import com.example.sbbackend.dto.RegisterRequestDto;
import com.example.sbbackend.services.AuthenticationService;
import com.example.sbbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static com.example.sbbackend.dto.Role.ADMIN;

@Configuration
public class AdminUserInitializer {
    @Value("${application.backend-url}")
    private String backendUrl;
    @Value("${application.admin.firstname}")
    private String firstname;
    @Value("${application.admin.lastname}")
    private String lastname;
    @Value("${application.admin.email}")
    private String email;
    @Value("${application.admin.password}")
    private String password;

@Autowired
    private   AuthenticationService authenticationService;



    @Bean
    public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            authenticationService.findByEmail(email).ifPresentOrElse(
                    user -> {
                        // Admin user already exists, no action needed
                    },
                    () -> {
                        // Create admin user if not found
                        RegisterRequestDto adminUser = RegisterRequestDto.builder()
                                .firstname(firstname)
                                .lastname(lastname)
                                .email(email)
                                .password(password)
                                .role(ADMIN)
                                .build();
                        authenticationService.registerAdmin(adminUser);
                    }
            );
        };
    }
}
