package com.example.sbbackend.services;

import com.example.sbbackend.dto.AuthRequestDto;
import com.example.sbbackend.dto.AuthResponse;
import com.example.sbbackend.dto.RegisterRequestDto;
import com.example.sbbackend.dto.Role;
import com.example.sbbackend.helpers.DuplicateEntryException;
import com.example.sbbackend.helpers.ForbiddenException;
import com.example.sbbackend.helpers.NetworkException;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.example.sbbackend.helpers.PostMapper.toAccountDto;
import static com.example.sbbackend.helpers.Utils.getCookieValue;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final LogoutService logoutService;
    private final MailNotificationsService mailNotificationsService;
    private final UserRepository userRepository;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${spring.mail.username}")
    private String systemEmail;
    @Value("${application.backend-url}")
    private String backendUrl;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            log.info(user.getEmail());
            return (user);
        }
        log.info("errorororororororoororo");
        return null;
    }
    public ResponseEntity<?> confirmToken(String token) {
      User   user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (user.getConfirmationTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token expired");
        }


        user.setConfirmed(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenExpiry(null);
        repository.save(user);


        return ResponseEntity.status(200).body("Account confirmed successfully");
    }
    public User registerAdmin(RegisterRequestDto request) {

        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdAt(new Date())
                .confirmed(true)
                .build();
        if (user.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }


        try {
            return repository.save(user);


        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntryException("Duplicate email.");
        } catch (NetworkException ex) { // Replace with actual network exception
            throw new NetworkException("Network error occurred.");
        } catch (ForbiddenException ex) { // Replace with actual forbidden access exception
            throw new ForbiddenException("You do not have permission to perform this action.");
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred.");
        }
    }
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
    public ResponseEntity<?> register(RegisterRequestDto request) {

        String token = UUID.randomUUID().toString();
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(new Date())
                .confirmationToken(token)
                .confirmationTokenExpiry(LocalDateTime.now().plusMonths(1))
                .confirmed(false)
                .build();
        if (user.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        try {
              repository.save(user);

            String confirmationLink =  backendUrl+"/api/v1/auth/confirm?token=" + token;
          return  mailNotificationsService.sendNotification(
                    systemEmail,
                    user.getEmail(),
                    "Account Confirmation",
                    "Please confirm your account by clicking the following link: " + confirmationLink
            );
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntryException("Duplicate email.");
        } catch (NetworkException ex) { // Replace with actual network exception
            throw new NetworkException("Network error occurred.");
        } catch (ForbiddenException ex) { // Replace with actual forbidden access exception
            throw new ForbiddenException("You do not have permission to perform this action.");
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred.");
        }
    }

    public ResponseEntity<?> updateExistingUser(String email, User userData) {
        User user = repository.findByEmail(email).orElse(null);
        User auth = getAuthenticatedUser();
        if (auth == null || user == null) return ResponseEntity.status(200).body("error");

        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(Objects.requireNonNull(auth).getId(), Objects.requireNonNull(user).getId())) {
            user.setLastname(userData.getLastname());
            user.setFirstname(userData.getFirstname());
            user.setEmail(userData.getEmail());
            user.setAddress(userData.getAddress());
            user.setMobile(userData.getMobile());
            user.setBio(userData.getBio());
            user.setBirthday(userData.getBirthday());
            if (userData.getAvatar() != null) {
                user.setAvatar(userData.getAvatar());
            }


            user.setUpdatedAt(new Date());

            repository.save(user);

            return ResponseEntity.status(200).body("user updated");
        } else {
            return ResponseEntity.status(403).body("you are not admin");
        }

    }

    public ResponseEntity<?> authenticate2(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
        var user = repository.findByEmail(email)
                .orElseThrow();
        return getAuthResponse(  user);
    }

    private ResponseEntity<?> getAuthResponse(  User user) {
        var jwtToken = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("none")
                .path("/")
                .maxAge(jwtExpiration)
                .build();

       // response.addHeader();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(200).headers(headers).body(user);
     //   AuthResponse.builder().user(toAccountDto(user)).build();
    }

    public ResponseEntity<?> authenticate(AuthRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        if (user.getConfirmed()) {
            return getAuthResponse( user);
        } else return ResponseEntity.status(403).body("plz check your email to confirm your account.");
    }

    public ResponseEntity<?> isAuthenticated(HttpServletRequest request, HttpServletResponse response) {

        String cookie = getCookieValue(request);

        if (jwtService.isTokenNotExpired(cookie)) {

            var user = repository.findByEmail(jwtService.extractUsername(cookie))
                    .orElseThrow();
            if (user.getConfirmed()) {
                return getAuthResponse(user);
            } else {
                logoutService.logout(request,response,SecurityContextHolder.getContext().getAuthentication());
                return ResponseEntity.status(403).body("plz check your email to confirm your account.");
            }
        } else {
            return null;
        }
    }
}
