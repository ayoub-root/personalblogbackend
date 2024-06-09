package com.example.sbbackend.controllers;

import com.example.sbbackend.dto.ForgotPasswordRequestDto;
import com.example.sbbackend.dto.ResetPasswordRequestDto;
import com.example.sbbackend.dto.Role;
import com.example.sbbackend.dto.UpdatePasswordRequestDto;
import com.example.sbbackend.models.SavedItems;
import com.example.sbbackend.models.User;
import com.example.sbbackend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")

public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;
    private User user;

    @ModelAttribute("authentication")
    public Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @PostMapping("/saveditems")
    public ResponseEntity<?> saveNewItem(@RequestBody SavedItems newItem) {
        System.out.println(newItem.getType());
        newItem.setId(UUID.randomUUID().toString());
        return userService.saveItemForUser(newItem);

    }

    @GetMapping("/saveditems")
    public ResponseEntity<?> getSavedItems() {

        ResponseEntity<?> ff = userService.getSavedItems();

        return ResponseEntity.status(200).body(ff); //

    }

    @DeleteMapping("/saveditems/{slug}")
    public ResponseEntity<?> removeSavedItem(@PathVariable String slug) {
        return userService.removeItemForUser(slug);
    }

    @GetMapping
    public ResponseEntity<Page<?>> getAll(@PageableDefault(
            page = 0,
            size = 10,
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        Page<?> usersPage = userService.getAll(pageable);
        if (usersPage.isEmpty()) {
            return ResponseEntity.status(403).body(usersPage);
        } else
            return ResponseEntity.status(200).body(usersPage);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long userId) {

        User auth = getAuthenticatedUser();

        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN && !Objects.equals(auth.getId(), userId)) {
            userService.deleteUser(userId);

            return ResponseEntity.status(200).body("user is deleted successfully");
        } else
            return ResponseEntity.status(403).body("you are not admin");

    }

    @PostMapping("/updatepassword/{userId}")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long userId,
            @RequestBody UpdatePasswordRequestDto request, HttpServletResponse response) {


        return ResponseEntity.ok(userService.changePassword(request, userId));
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(

            @RequestBody ForgotPasswordRequestDto data) {


        return userService.forgotPassword(data.getEmail());
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resttPassword(

            @RequestBody ResetPasswordRequestDto data) {


        return userService.resetPassword(data);
    }
}

