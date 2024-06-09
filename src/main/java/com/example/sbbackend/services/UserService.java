package com.example.sbbackend.services;

import com.example.sbbackend.dto.ResetPasswordRequestDto;
import com.example.sbbackend.dto.Role;
import com.example.sbbackend.dto.UpdatePasswordRequestDto;
import com.example.sbbackend.dto.UsersListDto;
import com.example.sbbackend.helpers.FileService;
import com.example.sbbackend.helpers.PostMapper;
import com.example.sbbackend.models.SavedItems;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${spring.mail.username}")
    private String systemEmail;
    @Value("${application.frontend-url}")
    private String frontendUrl;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final FileService fileService;
    private  final MailNotificationsService mailService;
    public ResponseEntity<?> forgotPassword(String email){

        Optional<User> user= repository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Email not found : "+email);
        }


        String token = UUID.randomUUID().toString();
        user.get().setPasswordToken(token);
        user.get().setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        repository.save(user.get());

        String resetLink = frontendUrl+"/account/resetpassword?token=" + token;
        String subject = "Password Reset Request";
        String body = "Click the link below to reset your password:\n" + resetLink;

   return  mailService.sendNotification(systemEmail, email, subject, body);







    }

    public ResponseEntity<?> resetPassword( ResetPasswordRequestDto data) {

        Optional<User> userOptional = repository.findByPasswordToken(data.getToken());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        User user = userOptional.get();
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(data.getNewPassword())); // Ideally, hash the password before saving
        user.setPasswordToken(null);
        user.setResetTokenExpiry(null);
        user.setUpdatedAt(new Date());
        repository.save(user);

        return ResponseEntity.ok("Password has been reset successfully");
    }
    public ResponseEntity<?> changePassword(UpdatePasswordRequestDto request, Long userId) {
        User loggedUser = getAuthenticatedUser();
        Optional<User> user = repository.findById(userId);
        if ((loggedUser != null && loggedUser.getRole() == Role.ADMIN && user.isPresent())
                || (user.isPresent() && Objects.equals(Objects.requireNonNull(loggedUser).getId(), userId))) {
            // check if the current password is correct

            if (!passwordEncoder.matches(request.getOldPassword(), user.get().getPassword())) {
                throw new IllegalStateException("Wrong password");
            }
//        // check if the two new passwords are the same
//        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
//            throw new IllegalStateException("Password are not the same");
//        }

            // update the password
            user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.get().setUpdatedAt(new Date());
            // save the new password
            repository.save(user.get());
            return ResponseEntity.status(200).body("password updated");
        } else return ResponseEntity.status(403).body("user not found");
    }

    public Optional<User> getAllByRole(Role role) {
        return repository.findByRole(role);
    }

    public Page<UsersListDto> getAll2(Pageable pageable) {
        User auth = getAuthenticatedUser();
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN) {
            Page<User> users = repository.findAll(pageable);
            return users.map(PostMapper::toUsersListDto);
        } else return Page.empty();
    }

    public Page<UsersListDto> getAll(Pageable pageable) {
        User auth = getAuthenticatedUser();
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN) {
            Page<User> users = repository.findAll(pageable);
            return users.map(PostMapper::toUsersListDto);
        } else {
            List<User> singleUserList = Collections.singletonList(auth);
            Page<User> singleUserPage = new PageImpl<>(singleUserList, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), 1);
            return singleUserPage.map(PostMapper::toUsersListDto);
        }
    }

    @Transactional
    public ResponseEntity<?> saveItemForUser(SavedItems savedItemDto) {
        User user = getAuthenticatedUser();

        if (user == null)
            return ResponseEntity.status(403).body("user not found");
        ;
        System.out.println(user.getUsername());
        user.addSavedItem(savedItemDto);
        user.setUpdatedAt(new Date());
        repository.save(user);

        return ResponseEntity.status(200).body(user);


    }

    @Transactional
    public ResponseEntity<?> getSavedItems() {
        User user = getAuthenticatedUser();

        if (user != null) {
            return ResponseEntity.status(200).body(user.getSavedItems());
        } else {
            return null;
        }
    }

    @Transactional
    public ResponseEntity<?> removeItemForUser(String itemValue) {
        User user = getAuthenticatedUser();

        if (user == null) return null;
        SavedItems itemToRemove = user.getSavedItemByValue(itemValue);
        if (itemToRemove != null) {
            user.removeSavedItem(itemToRemove);
            user.setUpdatedAt(new Date());
            repository.save(user);
            return ResponseEntity.status(200).body("Item is removed");

        } else {
            return ResponseEntity.status(404).body("Saved item not found");
        }

    }

    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException(" user not found"));

        if (user.getAvatar() != null) {
            String folder = "profiles"; // or whatever folder you use to store user photos
            boolean deleted = fileService.delete(user.getAvatar(), folder);
            if (!deleted) {
                // Handle the case where the photo could not be deleted
                // For example, log an error or throw an exception
                System.err.println("Failed to delete photo: " + user.getAvatar());
            }
        }
        repository.deleteById(id);
    }
}
